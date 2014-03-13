package io.benvol.elastic.client;

import io.benvol.model.ElasticHttpResponse;

public interface ElasticResponseCallback {
    public void execute(ElasticHttpResponse response);
}
