package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.ElasticHttpResponse;
import io.benvol.model.HttpKind;
import io.benvol.model.auth.AuthDirective;
import io.benvol.model.auth.AuthUser;
import io.benvol.model.auth.ConfirmKind;
import io.benvol.model.auth.ConfirmPredicate;
import io.benvol.model.auth.IdentifyPredicate;
import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.model.policy.Policy;
import io.benvol.util.JSON;
import io.benvol.util.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;

public class ElasticRestClient {

    private static final Logger LOG = Logger.getLogger(ElasticRestClient.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final BenvolioSettings _settings;
    private final List<KeyValuePair<String, Integer>> _elasticHosts;

    private final String _userTypeName;
    private final Set<String> _userIdentityFields;

    public ElasticRestClient(BenvolioSettings settings) {
        _settings = settings;
        _elasticHosts = settings.getElasticHosts();
        _userTypeName = _settings.getUserRemoteSchema().getElasticTypeName();
        _userIdentityFields = Sets.newHashSet(settings.getUserRemoteSchema().getIdentityFieldNames());
    }

    public void execute(ElasticHttpRequest request, ElasticResponseCallback callback) {
        KeyValuePair<String, Integer> host = chooseElasticHost();
        String url = request.makeUrl(host.getKey(), host.getValue());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Create an appropriate kind of HTTP request
            HttpUriRequest method;
            switch (request.getHttpKind()) {
                case GET: method = new HttpGet(url); break;
                case POST: method = new HttpPost(url); break;
                case PUT: method = new HttpPut(url); break;
                case DELETE: method = new HttpDelete(url); break;
                default: throw new RuntimeException("unsupported HttpKind: " + request.getHttpKind());
            }

            // Execute the request and generate the response
            try (CloseableHttpResponse response = httpClient.execute(method)) {

                // Check the response status
                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("handle all non-ok status codes"); // TODO
                }

                // Get all the response headers
                Header[] headers = response.getAllHeaders();

                // Get the response body as a string
                HttpEntity entity = response.getEntity();
                if (entity.getContentLength() > 0) {
                    try (InputStream content = entity.getContent()) {

                        // NOTE: ElasticSearch only produces UTF8
                        InputStreamReader reader = new InputStreamReader(content, Charsets.UTF_8);
                        String responseBody = CharStreams.toString(reader);

                        // Let the callback process the response data
                        callback.execute(new ElasticHttpResponse(request, status, headers, responseBody));
                    }
                }
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private KeyValuePair<String, Integer> chooseElasticHost() {
        int elasticHostCount = _elasticHosts.size();
        if (elasticHostCount == 1) {
            return _elasticHosts.get(0);
        } else if (elasticHostCount > 1) {
            int index = (int) Math.floor(Math.random() * elasticHostCount);
            return _elasticHosts.get(index);
        } else {
            throw new RuntimeException("no elastic hosts to choose from"); // TODO: CUSTOM EXCEPTION TYPE
        }
    }

    public AuthUser authenticate(AuthDirective authDirective) {

        // Make sure that the user is only attempting to identify herself using
        // an officially-sanctioned user-type-name and identity-field-names.
        for (IdentifyPredicate predicate : authDirective.getIdentifyPredicates()) {
            if (
                !_userTypeName.equals(predicate.getUserType()) ||
                !_userIdentityFields.contains(predicate.getQualifiedField())
            ) {
                throw new RuntimeException("authentication failure"); // TODO: CUSTOM EXCEPTION TYPE
            }
        }

        // Create an elastic request to identify this user
        UserRemoteSchema userRemoteSchema = _settings.getUserRemoteSchema();
        ElasticHttpRequest userRequest = new ElasticHttpRequest(
            HttpKind.POST,
            String.format(
                "/%s/%s/_search",
                Joiner.on(',').join(_settings.getIndexNames()),
                userRemoteSchema.getElasticTypeName()
            ),
            createSingleUserQuery(authDirective)
        );

        // Execute the query to find the user indicated in this AuthDirective
        ElasticHitCollector userHitCollector = new ElasticHitCollector();
        execute(userRequest, userHitCollector);

        // The IDENTIFY predicate of the AuthDirective must return exactly ONE user. If this predicate
        // could possibly apply to more than one user, then the entire request must fail.
        int userResultCount = userHitCollector.getTotalHitCount();
        if (userResultCount != 1) {
            throw new RuntimeException("authentication failure"); // TODO: CUSTOM EXCEPTION TYPE
        }

        // The rest of the authentication logic is based upon the user JSON object
        UserRemoteModel user = new UserRemoteModel(userHitCollector.getHits().get(0), userRemoteSchema);

        // Confirm the user's identity
        List<ConfirmPredicate> confirmPredicates = authDirective.getConfirmPredicates();
        if (confirmPredicates.isEmpty()) {
            throw new RuntimeException("authentication failure"); // TODO: CUSTOM EXCEPTION TYPE
        }
        for (ConfirmPredicate predicate : confirmPredicates) {
            ConfirmKind confirmKind = predicate.getConfirmKind();
            if (confirmKind.equals(ConfirmKind.PASSHASH)) {

                // Confirm the user identity using a salted-passhash
                String storedSalt = user.getSalt();
                String storedDoublePasshash = user.getPasshash();

                String allegedSinglePasshash = predicate.getOperand();
                String saltedPassword = storedSalt + allegedSinglePasshash;
                String allededDoublePasshash = Hashing.sha256().hashString(saltedPassword, Charsets.UTF_8).toString();
                if (!allededDoublePasshash.equals(storedDoublePasshash)) {
                    throw new RuntimeException("authentication failure"); // TODO: CUSTOM EXCEPTION TYPE
                }

            } else if (confirmKind.equals(ConfirmKind.TOKEN)) {
                // TODO: Confirm the user by searching for a session with the given token.
                // TODO: When & how should sessions be extended?
                throw new RuntimeException("Token-based authentication has not yet been implemented");
            }
        }

        // If we reach this point, the user has been successfully identified, and their identity has been confirmed.

        // TODO: determine actual group membership and resolve roles
        List<GroupRemoteModel> groups = Lists.newArrayList();
        List<RoleRemoteModel> roles = Lists.newArrayList();
        SessionRemoteModel session = null;

        return new AuthUser(user, groups, roles, session);
    }

    private ObjectNode createSingleUserQuery(AuthDirective authDirective) {
        return JSON.map(
            JSON.pair("from", 0),
            JSON.pair("size", 1),
            JSON.pair("query",
                JSON.uniMap(
                    "filtered", JSON.map(
                        JSON.pair("query", JSON.uniMap("match_all", JSON.map())),
                        JSON.pair("filter", createUserFilter(authDirective))
                    )
                )
            )
        );
    }

    private ObjectNode createUserFilter(AuthDirective authDirective) {
        ObjectNode filter = JSON.map();
        List<IdentifyPredicate> predicates = authDirective.getIdentifyPredicates();
        if (predicates.isEmpty()) {
            // TODO: return an anonymous AuthUser
        } else if (predicates.size() == 1) {
            IdentifyPredicate predicate = predicates.get(0);
            filter = JSON.uniMap(
                "term", JSON.map(
                    JSON.pair(predicate.getQualifiedField(), predicate.getOperand())
                )
            );
        } else {
            ArrayNode andClauses = JSON.list();
            for (IdentifyPredicate predicate : predicates) {
                andClauses.add(JSON.uniMap(
                    "term", JSON.map(
                        JSON.pair(predicate.getQualifiedField(), predicate.getOperand())
                    )
                ));
            }
            filter = JSON.uniMap("and", andClauses);
        }
        return filter;
    }

    public List<Policy> findPoliciesFor(AuthUser authUser, ElasticHttpRequest elasticHttpRequest) {
        throw new RuntimeException("NOT IMPLEMENTED"); // TODO
    }
}
