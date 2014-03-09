package io.benvol.model.auth.remote;

public abstract class AbstractRemoteSchema {

    private final String _elasticTypeName;
    private final String _idFieldName;
    private final String _idFieldKind;

    public AbstractRemoteSchema(
        String elasticTypeName,
        String idFieldName,
        String idFieldKind
    ) {
       _elasticTypeName = elasticTypeName;
       _idFieldName = idFieldName;
       _idFieldKind = idFieldKind;
    }

    public String getElasticTypeName() {
        return _elasticTypeName;
    }

    public String getIdFieldName() {
        return _idFieldName;
    }

    public String getIdFieldKind() {
        return _idFieldKind;
    }

}
