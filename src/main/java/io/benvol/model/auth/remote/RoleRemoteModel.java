package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RoleRemoteModel extends AbstractRemoteModel {

    // TODO: add field & getter for role names

    public RoleRemoteModel(ObjectNode json, RoleRemoteSchema roleRemoteSchema) {
        super(json, roleRemoteSchema);
    }

    public String getRoleId() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getIdFieldName());
    }

    @Override
    public RoleRemoteSchema getRemoteSchema() {
        return (RoleRemoteSchema) super.getRemoteSchema();
    }


}
