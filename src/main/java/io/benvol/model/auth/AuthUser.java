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

import com.google.common.collect.Lists;

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

    @Override
    public List<Policy> findPolicies(
        ElasticRestClient elasticRestClient,
        ElasticRequestFactory elasticRequestFactory
    ) {

        List<Policy> policies = Lists.newArrayList();

        // TODO: Perform an elasticsearch query to find candidate policies for this (potentially
        // anonymous) user and for this request. Then, eliminate candidate policies that don't
        // specifically apply to this request. This two-phase candidate-elimination process is
        // necessary because policies contain certain types of predicates that can't be matched
        // via any kind of elasticsearch query. (For example, using a regex in an indexed policy
        // to match against a string literal in a user query.)
        throw new RuntimeException("NOT IMPLEMENTED"); // TODO
    }

}
