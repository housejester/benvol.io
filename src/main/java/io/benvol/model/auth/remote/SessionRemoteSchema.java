package io.benvol.model.auth.remote;

import com.fasterxml.jackson.databind.JsonNode;

public class SessionRemoteSchema extends AbstractRemoteSchema {

    private final String _userIdFieldName;
    private final String _tokenFieldName;

    public SessionRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind,
        String userIdFieldName,
        String tokenFieldName
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
        _userIdFieldName = userIdFieldName;
        _tokenFieldName = tokenFieldName;
    }

    public String getUserIdFieldName() {
        return _userIdFieldName;
    }

    public String getTokenFieldName() {
        return _tokenFieldName;
    }

    public static SessionRemoteSchema fromConfigJson(JsonNode json) {
        return new SessionRemoteSchema(
            json.get("type_name").asText(),
            json.get("id_field_name").asText(),
            json.get("id_field_kind").asText(),
            json.get("user_id_field_name").asText(),
            json.get("token_field_name").asText()
        );
    }
}
