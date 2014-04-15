package io.benvol.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Throwables;

public class JSON {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static KeyValuePair<String, JsonNode> pair(String key, Object value) {
        return new KeyValuePair<String, JsonNode>(key, from(value));
    }

    public static ArrayNode list(Object... values) {
        ArrayNode array = OBJECT_MAPPER.createArrayNode();
        for (Object v : values) {
            array.add(from(v));
        }
        return array;
    }

    public static ArrayNode listNonNull(Object... values) {
        ArrayNode array = OBJECT_MAPPER.createArrayNode();
        for (Object v : values) {
            if (v!=null) {
                array.add(from(v));
            }
        }
        return array;
    }

    public static ObjectNode and(ArrayNode andClauses) {
        if (andClauses.size() == 0) {
            return map();
        }
        if (andClauses.size() == 1) {
            return (ObjectNode)andClauses.get(0);
        }
        return JSON.uniMap("and", andClauses);
    }

    public static ObjectNode uniMap(String key, Object value) {
        return map(pair(key, value));
    }

    @SafeVarargs
    public static ObjectNode map(KeyValuePair<String, JsonNode>... pairs) {
        ObjectNode object = OBJECT_MAPPER.createObjectNode();
        for (KeyValuePair<String, JsonNode> pair : pairs) {
            object.put(pair.getKey(), pair.getValue());
        }
        return object;
    }

    public static JsonNode from(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        } else if (value instanceof JsonNode) {
            return (JsonNode) value;
        } else if (value instanceof Jsonable) {
            return ((Jsonable) value).toJson();
        } else if (value instanceof String) {
            return new TextNode((String) value);
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? BooleanNode.TRUE : BooleanNode.FALSE;
        } else if (value instanceof Double) {
            return new DoubleNode((Double) value);
        } else if (value instanceof Float) {
            return new FloatNode((Float) value);
        } else if (value instanceof Long) {
            return new LongNode((Long) value);
        } else if (value instanceof Integer) {
            return new IntNode((Integer) value);
        } else if (value instanceof Short) {
            return new ShortNode((Short) value);
        } else if (value instanceof List) {
            ArrayNode array = OBJECT_MAPPER.createArrayNode();
            for (Object object : (List<Object>) value) {
                array.add(from(object));
            }
            return array;
        } else if (value instanceof Map) {
            ObjectNode map = OBJECT_MAPPER.createObjectNode();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                map.put(
                    entry.getKey().toString(),
                    from(entry.getValue())
                );
            }
            return map;
        }
        throw new RuntimeException("can't convert to JSON: " + value.toString());
    }

    public static String stringify(JsonNode value) {
        String stringified = null;
        try {
            stringified = OBJECT_MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return stringified;
    }
}
