package io.benvol.model.auth;

import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;

import java.net.InetAddress;
import java.util.List;

public abstract class ResolvedUser {

    private final InetAddress _ipAddress;

    public ResolvedUser(InetAddress ipAddress) {
        _ipAddress = ipAddress;
    }

    public InetAddress getIpAddress() {
        return _ipAddress;
    }

    public abstract boolean isAnonymous();
    public abstract UserRemoteModel getUser();
    public abstract List<GroupRemoteModel> getGroups();
    public abstract List<RoleRemoteModel> getRoles();
    public abstract SessionRemoteModel getSession();

}
