package io.benvol.model.query;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticQuery {

    private final ObjectNode _json;

    public ElasticQuery(ObjectNode json) {
        _json = json;
    }

    public ObjectNode getJson() {
        return _json;
    }

}
