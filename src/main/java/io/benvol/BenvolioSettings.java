package io.benvol;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class BenvolioSettings {
    
    private final String _environment;
    private final int _serverPort;
    private final int _threadPoolSize;

    public BenvolioSettings(ObjectNode json) {
        _environment = json.get("environment").asText();
        _serverPort = json.get("server").get("port").asInt();
        _threadPoolSize = json.get("thread_pool_size").asInt();
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
    
}
