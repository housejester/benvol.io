package io.benvol.util;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

public class JsonUtil {

    public static List<String> getStringListForKey(JsonNode json, String key) {
        List<String> list = Collections.emptyList();
        if (json.has(key)) {
            JsonNode value = json.get(key);
            if (value.isTextual()) {
                list = Collections.singletonList(value.asText());
            } else if (value.isArray()) {
                list = Lists.newArrayListWithExpectedSize(value.size());
                for (JsonNode child : (ArrayNode) value) {
                    list.add(child.asText());
                }
            } else {
                throw new RuntimeException(String.format(
                    "can't get string list for key '%s' from json: %s",
                    key, json.toString()
                ));
            }
        }
        return list;
    }

}
