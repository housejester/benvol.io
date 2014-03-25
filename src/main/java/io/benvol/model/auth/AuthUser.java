package io.benvol.model.auth;

import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;

import java.net.InetAddress;
import java.util.List;

public class AuthUser extends ResolvedUser {

    private final UserRemoteModel _user;
    private final List<GroupRemoteModel> _groups;
    private final List<RoleRemoteModel> _roles;
    private final SessionRemoteModel _session;

    public AuthUser(
        InetAddress ipAddress,
        UserRemoteModel user,
        List<GroupRemoteModel> groups,
        List<RoleRemoteModel> roles,
        SessionRemoteModel session
    ) {
        super(ipAddress);
        _user = user;
        _groups = groups;
        _roles = roles;
        _session = session;
    }

    public boolean isAnonymous() {
        return false;
    }

    public UserRemoteModel getUser() {
        return _user;
    }

    public List<GroupRemoteModel> getGroups() {
        return _groups;
    }

    public List<RoleRemoteModel> getRoles() {
        return _roles;
    }

    public SessionRemoteModel getSession() {
        return _session;
    }

}
