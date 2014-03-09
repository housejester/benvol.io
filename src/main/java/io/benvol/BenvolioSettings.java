package io.benvol;

import io.benvol.util.KeyValuePair;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.common.collect.Lists;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BenvolioSettings {

    private final String _environment;
    private final int _serverPort;
    private final int _threadPoolSize;

    private final List<KeyValuePair<String, Integer>> _elasticHosts;

    private final List<String> _indexNames;

    private final String _userTypeName;
    private final String _sessionTypeName;
    private final String _groupTypeName;
    private final String _roleTypeName;

    public BenvolioSettings(ObjectNode json) {

        // Basic top-level settings
        _environment = json.get("environment").asText();
        _serverPort = json.get("server").get("port").asInt();
        _threadPoolSize = json.get("thread_pool_size").asInt();

        // Elastic hostnames and ports
        List<KeyValuePair<String, Integer>> elasticHosts = Lists.newArrayList();
        for (JsonNode node : ((ArrayNode) json.get("elastic").get("hosts"))) {
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
        _userTypeName = json.get("elastic").get("auth").get("user").get("type_name").asText();
        _sessionTypeName = json.get("elastic").get("auth").get("session").get("type_name").asText();
        _groupTypeName = json.get("elastic").get("auth").get("group").get("type_name").asText();
        _roleTypeName = json.get("elastic").get("auth").get("role").get("type_name").asText();
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

    public String getUserTypeName() {
        return _userTypeName;
    }

    public String getSessionTypeName() {
        return _sessionTypeName;
    }

    public String getGroupTypeName() {
        return _groupTypeName;
    }

    public String getRoleTypeName() {
        return _roleTypeName;
    }

}
