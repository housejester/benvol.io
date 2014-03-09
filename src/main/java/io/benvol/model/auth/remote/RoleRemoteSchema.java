package io.benvol.model.auth.remote;

import com.fasterxml.jackson.databind.JsonNode;

public class RoleRemoteSchema extends AbstractRemoteSchema {

    private final String _nameFieldName;

    public RoleRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind,
        String nameFieldName
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
        _nameFieldName = nameFieldName;
    }

    public String getNameFieldName() {
        return _nameFieldName;
    }

    public static RoleRemoteSchema fromConfigJson(JsonNode json) {
        return new RoleRemoteSchema(
            json.get("type_name").asText(),
            json.get("id_field_name").asText(),
            json.get("id_field_kind").asText(),
            json.get("name_field_name").asText()
        );
    }
}
