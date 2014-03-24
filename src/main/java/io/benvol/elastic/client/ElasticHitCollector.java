package io.benvol.elastic.client;

import io.benvol.model.ElasticHttpResponse;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ElasticHitCollector implements ElasticResponseCallback {

    private int _totalHitCount;
    private List<ObjectNode> _hits;

    public ElasticHitCollector() {
        _hits = Lists.newArrayList();
    }

    @Override
    public void execute(ElasticHttpResponse response) {
        _totalHitCount = 0;
        JsonNode json = response.getResponseBodyAsJson();
        if (json.has("hits")) {
            ObjectNode hitsNode = (ObjectNode) json.get("hits");
            _totalHitCount = hitsNode.get("total").asInt();
            if (hitsNode.has("hits")) {
                ArrayNode hitsArray = (ArrayNode) hitsNode.get("hits");
                for (int i = 0, len = hitsArray.size(); i < len; i++) {
                    ObjectNode hit = (ObjectNode) hitsArray.get(i);
                    _hits.add(hit);
                }
            }
        }
    }

    public int getTotalHitCount() {
        return _totalHitCount;
    }

    public List<ObjectNode> getHits() {
        return _hits;
    }

}
