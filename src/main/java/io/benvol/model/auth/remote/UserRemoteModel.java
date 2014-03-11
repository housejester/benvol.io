package io.benvol.model.auth.remote;

import io.benvol.util.JsonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserRemoteModel extends AbstractRemoteModel {

    // TODO: add roles and groups...

    public UserRemoteModel(ObjectNode json, UserRemoteSchema userRemoteSchema) {
        super(json, userRemoteSchema);
    }

    public String getUserId() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getIdFieldName());
    }

    public String getPasshash() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getPasshashFieldName());
    }

    public String getSalt() {
        return JsonUtil.getStringForKey(getJson(), getRemoteSchema().getSaltFieldName());
    }

    @Override
    public UserRemoteSchema getRemoteSchema() {
        return (UserRemoteSchema) super.getRemoteSchema();
    }


}
