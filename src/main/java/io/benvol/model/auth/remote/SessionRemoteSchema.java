package io.benvol.model.auth.remote;

public class SessionRemoteSchema extends AbstractRemoteSchema {

    private final String _tokenFieldName;

    public SessionRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind,
        String tokenFieldName
    ) {
        super(elasticTypeName, idFieldName, idFieldKind);
        _tokenFieldName = tokenFieldName;
    }

    public String getTokenFieldName() {
        return _tokenFieldName;
    }
}
