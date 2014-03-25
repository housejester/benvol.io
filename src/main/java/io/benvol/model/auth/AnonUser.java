package io.benvol.model.auth;

import io.benvol.elastic.client.ElasticRequestFactory;
import io.benvol.elastic.client.ElasticRestClient;
import io.benvol.model.auth.remote.GroupRemoteModel;
import io.benvol.model.auth.remote.RoleRemoteModel;
import io.benvol.model.auth.remote.SessionRemoteModel;
import io.benvol.model.auth.remote.UserRemoteModel;
import io.benvol.model.policy.Policy;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

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
