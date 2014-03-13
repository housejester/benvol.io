package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.util.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;

public class ElasticRestClient {

    private static final Logger LOG = Logger.getLogger(ElasticRestClient.class);
    
    private static final MultiThreadedHttpConnectionManager CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private final List<KeyValuePair<String, Integer>> _elasticHosts;
    
    public ElasticRestClient(BenvolioSettings settings) {
        _elasticHosts = settings.getElasticHosts();
    }

    public ObjectNode execute(ElasticHttpRequest request) {
        KeyValuePair<String, Integer> host = chooseElasticHost();
        String url = request.makeUrl(host.getKey(), host.getValue());
        HttpClient client = new HttpClient(CONNECTION_MANAGER);
        HttpMethod method;
        switch (request.getHttpKind()) {
            case GET: method = new GetMethod(url); break;
            case POST: method = new PostMethod(url); break;
            case PUT: method = new PutMethod(url); break;
            case DELETE: method = new DeleteMethod(url); break;
            default: throw new RuntimeException("unsupported HttpKind: " + request.getHttpKind());
        }
        try {
            ObjectNode json = null;
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                try (InputStream inputStream = method.getResponseBodyAsStream()) {
                    json = (ObjectNode) OBJECT_MAPPER.readValue(inputStream, JsonNode.class);
                }
            } else {
                LOG.error(String.format("HTTP status code %s from ElasticSearch on request: %s", status, url));
            }
            return json;
        } catch (IOException e) {
            Throwables.propagate(e);
        } finally {
            try {
                if (method != null) method.releaseConnection();
            } catch (Throwable t) {
                LOG.error(t);
            }
        }
        return null;
    }

    private KeyValuePair<String, Integer> chooseElasticHost() {
        int elasticHostCount = _elasticHosts.size();
        if (elasticHostCount == 1) {
            return _elasticHosts.get(0);
        } else if (elasticHostCount > 1) {
            int index = (int) Math.floor(Math.random() * elasticHostCount);
            return _elasticHosts.get(index);
        } else {
            throw new RuntimeException("no elastic hosts to choose from");
        }
    }
}
