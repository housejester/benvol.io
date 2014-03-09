package io.benvol.model.auth.remote;

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
}
