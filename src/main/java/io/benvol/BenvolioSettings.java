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
    
}
