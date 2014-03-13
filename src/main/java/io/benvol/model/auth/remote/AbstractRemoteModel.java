package io.benvol.model.auth.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AbstractRemoteModel {

    // Auth objects come from ElasticSearch, where they are represented as JSON. Although the
    // Benvolio authentication system only interested in a few of the properties (usually:
    // foreign keys to groups, sessions, and roles), the policy system might need to enforce
    // policies based on some of the other properties stored in the customized user mappings
    // defined in the underlying index.
    private final ObjectNode _json;

    // The remote schema tells us which field names in the remote ElasticSearch mapping refer
    // to the different components (field names & types, etc) of an authentication system.
    private final AbstractRemoteSchema _remoteSchema;

    public AbstractRemoteModel(ObjectNode json, AbstractRemoteSchema remoteSchema) {
        _json = json;
        _remoteSchema = remoteSchema;
    }

    public ObjectNode getJson() {
        return _json;
    }

    public AbstractRemoteSchema getRemoteSchema() {
        return _remoteSchema;
    }

}
