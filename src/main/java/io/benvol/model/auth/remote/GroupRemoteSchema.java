package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class GroupRemoteSchema extends AbstractRemoteSchema {

    private final String _roleIdsFieldName;

    public GroupRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind,
        String roleIdsFieldName
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
        _roleIdsFieldName = roleIdsFieldName;
    }

    public String getRoleIdsFieldName() {
        return _roleIdsFieldName;
    }

    public static GroupRemoteSchema fromConfigJson(JsonNode json) {
        return new GroupRemoteSchema(
            json.get("type_name").asText(),
            json.get("id_field_name").asText(),
            json.get("id_field_kind").asText(),
            json.get("role_ids_field_name").asText()
        );
    }
}
