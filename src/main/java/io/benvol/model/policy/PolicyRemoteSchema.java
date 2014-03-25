package io.benvol.model.policy;

import io.benvol.model.auth.remote.AbstractRemoteSchema;

import com.fasterxml.jackson.databind.JsonNode;

public class PolicyRemoteSchema extends AbstractRemoteSchema {

    public PolicyRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
    }

    public static PolicyRemoteSchema fromConfigJson(JsonNode json) {
        return new PolicyRemoteSchema(
            json.get("type_name").asText(),
            json.get("id_field_name").asText(),
            json.get("id_field_kind").asText()
        );
    }
}
