package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.util.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticRestClient {

    private static final Logger LOG = Logger.getLogger(ElasticRestClient.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final List<KeyValuePair<String, Integer>> _elasticHosts;

    public ElasticRestClient(BenvolioSettings settings) {
        _elasticHosts = settings.getElasticHosts();
    }

    public ObjectNode execute(ElasticHttpRequest request) {
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

                // Get the response body
                HttpEntity entity = response.getEntity();
                try (InputStream content = entity.getContent()) {
                    return (ObjectNode) OBJECT_MAPPER.readTree(content);
                }
            }
        } catch (IOException e) {
            LOG.error(e);
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
