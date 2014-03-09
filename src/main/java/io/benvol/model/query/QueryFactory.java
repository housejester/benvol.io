package io.benvol.model.query;

import io.benvol.BenvolioSettings;
import io.benvol.model.auth.remote.GroupRemoteSchema;
import io.benvol.model.auth.remote.RoleRemoteSchema;
import io.benvol.model.auth.remote.SessionRemoteSchema;
import io.benvol.model.auth.remote.UserRemoteSchema;

import java.util.List;

public class QueryFactory {

    private final List<String> _indexNames;

    private final UserRemoteSchema _userRemoteSchema;
    private final GroupRemoteSchema _groupRemoteSchema;
    private final SessionRemoteSchema _sessionRemoteSchema;
    private final RoleRemoteSchema _roleRemoteSchema;

    public QueryFactory(BenvolioSettings settings) {
        _indexNames = settings.getIndexNames();
        _userRemoteSchema = settings.getUserRemoteSchema();
        _groupRemoteSchema = settings.getGroupRemoteSchema();
        _sessionRemoteSchema = settings.getSessionRemoteSchema();
        _roleRemoteSchema = settings.getRoleRemoteSchema();
    }

}
