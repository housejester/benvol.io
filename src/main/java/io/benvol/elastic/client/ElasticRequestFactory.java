package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.HttpKind;
import io.benvol.model.auth.AuthDirective;
import io.benvol.model.auth.IdentifyPredicate;
import io.benvol.model.auth.remote.GroupRemoteSchema;
import io.benvol.model.auth.remote.RoleRemoteSchema;
import io.benvol.model.auth.remote.SessionRemoteSchema;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.util.JSON;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;

public class ElasticRequestFactory {
    
    private final List<String> _indexNames;

    private final UserRemoteSchema _userRemoteSchema;
    private final GroupRemoteSchema _groupRemoteSchema;
    private final SessionRemoteSchema _sessionRemoteSchema;
    private final RoleRemoteSchema _roleRemoteSchema;

    public ElasticRequestFactory(BenvolioSettings settings) {
        _indexNames = settings.getIndexNames();
        _userRemoteSchema = settings.getUserRemoteSchema();
        _groupRemoteSchema = settings.getGroupRemoteSchema();
        _sessionRemoteSchema = settings.getSessionRemoteSchema();
        _roleRemoteSchema = settings.getRoleRemoteSchema();
    }
    
    public ElasticHttpRequest createSingleUserElasticRequest(AuthDirective authDirective) {
        ElasticHttpRequest singleUserRequest = new ElasticHttpRequest(
            HttpKind.POST,
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
}
