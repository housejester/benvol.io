package io.benvol.model.auth;

import io.benvol.elastic.client.ElasticHitCollector;
import io.benvol.elastic.client.ElasticRequestFactory;
import io.benvol.elastic.client.ElasticRestClient;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.auth.fail.AuthFailure;
import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.util.RandomString;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

public class AuthDirective {

    private final InetAddress _ipAddress;
    private final List<IdentifyPredicate> _identifyPredicates;
    private final List<ConfirmPredicate> _confirmPredicates;

    public AuthDirective(InetAddress ipAddress, Map<String, String[]> headers) {
        _ipAddress = ipAddress;
        _identifyPredicates = IdentifyPredicate.fromHeaders(headers);
        _confirmPredicates = ConfirmPredicate.fromHeaders(headers);
    }

    public boolean isAnonymous() {
        return _identifyPredicates.isEmpty();
    }

    public List<IdentifyPredicate> getIdentifyPredicates() {
        return _identifyPredicates;
    }

    public List<ConfirmPredicate> getConfirmPredicates() {
        return _confirmPredicates;
    }

    public ResolvedUser authenticate(ElasticRestClient client, UserRemoteSchema userRemoteSchema, ElasticRequestFactory elasticQueryFactory) {

        // When a user attempts to execute a query without any authentication predicates
        // in their request header, then we should authenticate that request as an
        // anonymous user, based on whichever policies allow anonymous requests.
        if (_identifyPredicates.isEmpty() && _confirmPredicates.isEmpty()) {
            return new AnonUser(_ipAddress);
        }

        // Make sure that the user is only attempting to identify herself using
        // an officially-sanctioned user-type-name and identity-field-names.
        for (IdentifyPredicate predicate : _identifyPredicates) {
            if (
                !userRemoteSchema.getElasticTypeName().equals(predicate.getUserType()) ||
                !userRemoteSchema.getIdentityFieldNames().contains(predicate.getQualifiedField())
            ) {
                throw new AuthFailure(String.format(
                    "invalid auth key: %s.%s",
                    predicate.getUserType(), predicate.getQualifiedField()
                ));
            }
        }

        // Create an elastic request to identify this user
        ElasticHttpRequest userRequest = elasticQueryFactory.createSingleUserElasticRequest(this);

        // Execute the query to find the user indicated in this AuthDirective
        ElasticHitCollector userHitCollector = new ElasticHitCollector();
        client.execute(userRequest, userHitCollector);

        // The IDENTIFY predicate of the AuthDirective must return exactly ONE user. If this predicate
        // could possibly apply to more than one user, then the entire request must fail.
        int userResultCount = userHitCollector.getTotalHitCount();
        if (userResultCount != 1) {
            throw new AuthFailure("cannot uniquely identify user");
        }

        // The rest of the authentication logic is based upon the user JSON object
        UserRemoteModel user = new UserRemoteModel(userHitCollector.getHits().get(0), userRemoteSchema);

        // Confirm the user's identity
        List<ConfirmPredicate> confirmPredicates = getConfirmPredicates();
        if (confirmPredicates.isEmpty()) {
            throw new AuthFailure("cannot confirm user identity");
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
                    throw new AuthFailure("cannot confirm user identity");
                }

                // TODO: store a new session in elasticsearch, and return the session TOKEN in the response headers.
                // TODO: what about session TTL and expiry?
                String sessionToken = RandomString.generate(64);

            } else if (confirmKind.equals(ConfirmKind.TOKEN)) {
                // TODO: Confirm the user by searching for a session with the given token.
                // TODO: When & how should sessions be extended?
                // TODO: Lookup sessions in a local in-memory cache, since these will already
                // include user/group/role info, saving multiple ElasticSearch round-trips.
                // TODO: always invalidate expired cache entries immediately before the cache lookup
                throw new RuntimeException("Token-based authentication has not yet been implemented");
            }
        }

        // If we reach this point, the user has been successfully identified, and their identity has been confirmed.

        // TODO: determine actual group membership and resolve roles
        List<GroupRemoteModel> groups = Lists.newArrayList();
        List<RoleRemoteModel> roles = Lists.newArrayList();
        SessionRemoteModel session = null;

        AuthUser authUser = new AuthUser(_ipAddress, user, groups, roles, session);

        // TODO: Store this authUser in a local cache (keyed by session token), so that subsequent
        // authentications can be performed without having to issue so many elastic queries.

        return authUser;
    }
}
