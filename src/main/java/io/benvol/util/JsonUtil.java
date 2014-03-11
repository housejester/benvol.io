package io.benvol.util;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

public class JsonUtil {

    public static JsonNode findJsonNodeByFullyQualifiedName(JsonNode json, String name) {
        JsonNode currentNode = json;
        for (String nameSegment : name.split("\\.")) {
            if (currentNode.has(nameSegment)) {
                currentNode = currentNode.get(nameSegment);
            } else {
                return null;
            }
        }
        return currentNode;
    }

    public static String getStringForKey(JsonNode json, String key) {
        JsonNode node = findJsonNodeByFullyQualifiedName(json, key);
        if (node == null) {
            return null;
        } else if (node.isTextual()) {
            return node.asText();
        } else {
            throw new RuntimeException(String.format(
                "can't get string for key '%s' from json: %s",
                key, json.toString()
            ));
        }
    }

    public static List<String> getStringListForKey(JsonNode json, String key) {
        List<String> list = Collections.emptyList();
        JsonNode node = findJsonNodeByFullyQualifiedName(json, key);
        if (node != null) {
            if (node.isTextual()) {
                list = Collections.singletonList(node.asText());
            } else if (node.isArray()) {
                list = Lists.newArrayListWithExpectedSize(node.size());
                for (JsonNode child : (ArrayNode) node) {
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
