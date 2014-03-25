package io.benvol.elastic.client;

import io.benvol.BenvolioSettings;
import io.benvol.model.ElasticHttpRequest;
import io.benvol.model.ElasticHttpResponse;
import io.benvol.util.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class ElasticRestClient {

    private static final Logger LOG = Logger.getLogger(ElasticRestClient.class);

    private final List<KeyValuePair<String, Integer>> _elasticHosts;

    public ElasticRestClient(BenvolioSettings settings) {
        _elasticHosts = settings.getElasticHosts();
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
}
