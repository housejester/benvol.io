package io.benvol.util;

import com.fasterxml.jackson.databind.JsonNode;

public interface Jsonable {
    public JsonNode toJson();
}
