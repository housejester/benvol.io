package io.benvol.model.auth.remote;

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
}
