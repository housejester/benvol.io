package io.benvol.model.auth;

import io.benvol.elastic.client.ElasticRequestFactory;
import io.benvol.elastic.client.ElasticRestClient;
import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;
import io.benvol.model.policy.Policy;

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

    public abstract List<Policy> findPolicies(
        ElasticRestClient elasticRestClient,
        ElasticRequestFactory elasticRequestFactory
    );
}
