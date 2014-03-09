package io.benvol.model.query;

import io.benvol.BenvolioSettings;

import java.util.List;

public class QueryFactory {
    
    private final List<String> _indexNames;

    private final String _userType;
    private final String _groupType;
    private final String _sessionType;
    private final String _roleType;
    
    public QueryFactory(BenvolioSettings settings) {
        _indexNames = settings.getIndexNames();
        _userType = settings.getUserTypeName();
        _groupType = settings.getGroupTypeName();
        _sessionType = settings.getSessionTypeName();
        _roleType = settings.getRoleTypeName();
    }

}
