package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RoleRemoteModel extends AbstractRemoteModel {

    // TODO: add field & getter for role names

    public RoleRemoteModel(ObjectNode json, RoleRemoteSchema userRemoteSchema) {
        super(json, userRemoteSchema);
    }

    public String getRoleId() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getIdFieldName());
    }

    @Override
    public RoleRemoteSchema getRemoteSchema() {
        return (RoleRemoteSchema) super.getRemoteSchema();
    }


}
