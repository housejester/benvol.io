package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.HttpKind;
import io.benvol.model.auth.AnonUser;
import io.benvol.model.auth.AuthDirective;
import io.benvol.model.auth.IdentifyPredicate;
import io.benvol.model.auth.ResolvedUser;
import io.benvol.model.auth.remote.GroupRemoteSchema;
import io.benvol.model.auth.remote.RoleRemoteSchema;
import io.benvol.model.auth.remote.SessionRemoteSchema;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.model.policy.PolicyRemoteSchema;
import io.benvol.util.JSON;

import java.net.Inet4Address;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.net.InetAddresses;

public class ElasticRequestFactory {

    private final List<String> _indexNames;

    private final UserRemoteSchema _userRemoteSchema;
    private final GroupRemoteSchema _groupRemoteSchema;
    private final SessionRemoteSchema _sessionRemoteSchema;
    private final RoleRemoteSchema _roleRemoteSchema;
    private final PolicyRemoteSchema _policyRemoteSchema;

    public ElasticRequestFactory(BenvolioSettings settings) {
        _indexNames = settings.getIndexNames();
        _userRemoteSchema = settings.getUserRemoteSchema();
        _groupRemoteSchema = settings.getGroupRemoteSchema();
        _sessionRemoteSchema = settings.getSessionRemoteSchema();
        _roleRemoteSchema = settings.getRoleRemoteSchema();
        _policyRemoteSchema = settings.getPolicyRemoteSchema();
    }

    public ElasticHttpRequest createPolicyElasticRequest(AnonUser user) {
        ElasticHttpRequest singleUserRequest = new ElasticHttpRequest(
            HttpKind.POST,
            null,
            String.format(
                "/%s/%s/_search",
                Joiner.on(',').join(_indexNames),
                _policyRemoteSchema.getElasticTypeName()
            ),
            createAnonPolicyQuery(user)
        );
        return singleUserRequest;
    }

    public ElasticHttpRequest createSingleUserElasticRequest(AuthDirective authDirective) {
        ElasticHttpRequest singleUserRequest = new ElasticHttpRequest(
            HttpKind.POST,
            null,
            String.format(
                "/%s/%s/_search",
                Joiner.on(',').join(_indexNames),
                _userRemoteSchema.getElasticTypeName()
            ),
            createSingleUserQuery(authDirective)
        );
        return singleUserRequest;
    }

    public ObjectNode createSingleUserQuery(AuthDirective authDirective) {
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

    public ObjectNode createUserFilter(AuthDirective authDirective) {
        ObjectNode filter = JSON.map();
        List<IdentifyPredicate> predicates = authDirective.getIdentifyPredicates();
        if (predicates.size() == 1) {
            IdentifyPredicate predicate = predicates.get(0);
            filter = JSON.uniMap(
                "term", JSON.uniMap(predicate.getQualifiedField(), predicate.getOperand())
            );
        } else if (!predicates.isEmpty()) {
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

    private ObjectNode createAnonPolicyQuery(AnonUser anon) {
        return JSON.map(
            JSON.pair("from", 0),
            JSON.pair("size", 10_000), // TODO: set a configurable maximum number of policies per query?
            JSON.pair("query",
                JSON.uniMap(
                    "filtered", JSON.map(
                        JSON.pair("query", JSON.uniMap("match_all", JSON.map())),
                        JSON.pair("filter", createAnonPolicyFilter(anon))
                    )
                )
            )
        );
    }

    private ObjectNode createAnonPolicyFilter(AnonUser anon) {
        return JSON.uniMap(
            "or", JSON.list(
                JSON.uniMap("term", JSON.uniMap("match.anonymous", true)),
                createIpAddressFilter(anon)
            )
        );
    }

    private ObjectNode createIpAddressFilter(ResolvedUser user) {
        Inet4Address ip4Address = InetAddresses.getCoercedIPv4Address(user.getIpAddress());
        String ip4AddresString = ip4Address.getHostAddress();
        return JSON.uniMap(
            "nested", JSON.map(
                JSON.pair("path", "ip_ranges"),
                JSON.pair("filter", JSON.pair(
                    "and", JSON.list(
                        JSON.uniMap("range", JSON.uniMap("ip_ranges.min", JSON.uniMap("lte", ip4AddresString))),
                        JSON.uniMap("range", JSON.uniMap("ip_ranges.max", JSON.uniMap("gte", ip4AddresString)))
                    )
                )
            ))
        );
    }
}
