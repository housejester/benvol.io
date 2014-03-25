package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

public class UserRemoteSchema extends AbstractRemoteSchema {

    private final Set<String> _identityFieldNames;
    private final String _roleIdsFieldName;
    private final String _groupIdsFieldName;

    private final String _passhashFieldName;
    private final String _saltFieldName;

    public UserRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind,
        Set<String> identityFieldNames,
        String roleIdsFieldName,
        String groupIdsFieldName,
        String passhashFieldName,
        String saltFieldName
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
        _identityFieldNames = Collections.unmodifiableSet(identityFieldNames);
        _roleIdsFieldName = roleIdsFieldName;
        _groupIdsFieldName = groupIdsFieldName;
        _passhashFieldName = passhashFieldName;
        _saltFieldName = saltFieldName;
    }

    public Set<String> getIdentityFieldNames() {
        return _identityFieldNames;
    }

    public String getRoleIdsFieldName() {
        return _roleIdsFieldName;
    }

    public String getGroupIdsFieldName() {
        return _groupIdsFieldName;
    }

    public String getPasshashFieldName() {
        return _passhashFieldName;
    }

    public String getSaltFieldName() {
        return _saltFieldName;
    }

    public static UserRemoteSchema fromConfigJson(JsonNode json) {
        return new UserRemoteSchema(
            json.get("type_name").asText(),
            json.get("id_field_name").asText(),
            json.get("id_field_kind").asText(),
            Sets.newHashSet(JsonUtil.getStringListForKey(json, "identity_fields")),
            json.get("role_ids_field_name").asText(),
            json.get("group_ids_field_name").asText(),
            json.get("passhash_field_name").asText(),
            json.get("salt_field_name").asText()
        );
    }
}
