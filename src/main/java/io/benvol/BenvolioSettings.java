package io.benvol;

import io.benvol.model.auth.remote.GroupRemoteSchema;
import io.benvol.model.auth.remote.RoleRemoteSchema;
import io.benvol.model.auth.remote.SessionRemoteSchema;
import io.benvol.model.auth.remote.UserRemoteSchema;
import io.benvol.model.policy.PolicyRemoteSchema;
import io.benvol.util.KeyValuePair;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class BenvolioSettings {

    private final String _environment;
    private final int _serverPort;
    private final int _threadPoolSize;

    private final List<KeyValuePair<String, Integer>> _elasticHosts;

    private final List<String> _indexNames;

    private final UserRemoteSchema _userRemoteSchema;
    private final GroupRemoteSchema _groupRemoteSchema;
    private final SessionRemoteSchema _sessionRemoteSchema;
    private final RoleRemoteSchema _roleRemoteSchema;
    private final PolicyRemoteSchema _policyRemoteSchema;

    public BenvolioSettings(ObjectNode json) {

        // Basic top-level settings
        _environment = json.get("environment").asText();
        _serverPort = json.get("server").get("port").asInt();
        _threadPoolSize = json.get("thread_pool_size").asInt();

        // Elastic hostnames and ports
        List<KeyValuePair<String, Integer>> elasticHosts = Lists.newArrayList();
        for (JsonNode node : ((ArrayNode) json.get("elastic").get("nodes"))) {
            String hostname = node.get("hostname").asText();
            int restPort = node.get("rest_port").asInt();
            elasticHosts.add(new KeyValuePair<>(hostname, restPort));
        }
        _elasticHosts = Collections.unmodifiableList(elasticHosts);

        // Elastic index names
        List<String> indexNames = Lists.newArrayList();
        for (JsonNode node : ((ArrayNode) json.get("elastic").get("index_names"))) {
            indexNames.add(node.asText());
        }
        _indexNames = Collections.unmodifiableList(indexNames);

        // Well-known types in the authentication system
        JsonNode remoteSchemas = json.get("elastic").get("auth");
        _userRemoteSchema = UserRemoteSchema.fromConfigJson(remoteSchemas.get("user"));
        _groupRemoteSchema = GroupRemoteSchema.fromConfigJson(remoteSchemas.get("group"));
        _sessionRemoteSchema = SessionRemoteSchema.fromConfigJson(remoteSchemas.get("session"));
        _roleRemoteSchema = RoleRemoteSchema.fromConfigJson(remoteSchemas.get("role"));
        _policyRemoteSchema = PolicyRemoteSchema.fromConfigJson(remoteSchemas.get("policy"));
    }

    public String getEnvironment() {
        return _environment;
    }

    public int getThreadPoolSize() {
        return _threadPoolSize;
    }

    public int getServerPort() {
        return _serverPort;
    }

    public List<KeyValuePair<String, Integer>> getElasticHosts() {
        return _elasticHosts;
    }

    public List<String> getIndexNames() {
        return _indexNames;
    }

    public UserRemoteSchema getUserRemoteSchema() {
        return _userRemoteSchema;
    }

    public SessionRemoteSchema getSessionRemoteSchema() {
        return _sessionRemoteSchema;
    }

    public GroupRemoteSchema getGroupRemoteSchema() {
        return _groupRemoteSchema;
    }

    public RoleRemoteSchema getRoleRemoteSchema() {
        return _roleRemoteSchema;
    }

    public PolicyRemoteSchema getPolicyRemoteSchema() {
        return _policyRemoteSchema;
    }

}
