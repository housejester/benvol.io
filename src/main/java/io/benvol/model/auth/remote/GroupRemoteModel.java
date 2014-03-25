package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class GroupRemoteModel extends AbstractRemoteModel {

    // TODO: add field & getter for group names
    // TODO: add roles...

    public GroupRemoteModel(ObjectNode json, GroupRemoteSchema groupRemoteSchema) {
        super(json, groupRemoteSchema);
    }

    public String getGroupId() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getIdFieldName());
    }

    @Override
    public GroupRemoteSchema getRemoteSchema() {
        return (GroupRemoteSchema) super.getRemoteSchema();
    }


}
