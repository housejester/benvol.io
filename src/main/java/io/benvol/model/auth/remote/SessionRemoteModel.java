package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SessionRemoteModel extends AbstractRemoteModel {

    // TODO: add field & getter for role names

    public SessionRemoteModel(ObjectNode json, SessionRemoteSchema sessionRemoteSchema) {
        super(json, sessionRemoteSchema);
    }

    public String getRoleId() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getIdFieldName());
    }

    @Override
    public SessionRemoteSchema getRemoteSchema() {
        return (SessionRemoteSchema) super.getRemoteSchema();
    }


}
