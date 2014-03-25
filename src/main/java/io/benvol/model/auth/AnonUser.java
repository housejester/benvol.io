package io.benvol.model.auth;

import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class AnonUser extends ResolvedUser {

    public AnonUser(InetAddress ipAddress) {
        super(ipAddress);
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }

    @Override
    public UserRemoteModel getUser() {
        return null;
    }

    @Override
    public List<GroupRemoteModel> getGroups() {
        return Collections.<GroupRemoteModel>emptyList();
    }

    @Override
    public List<RoleRemoteModel> getRoles() {
        return Collections.<RoleRemoteModel>emptyList();
    }

    @Override
    public SessionRemoteModel getSession() {
        return null; // TODO: return an AnonSession object?
    }

}
