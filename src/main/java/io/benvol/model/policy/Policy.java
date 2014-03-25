package io.benvol.model.policy;

import java.util.List;

import com.google.common.collect.Lists;

public class Policy {
    
    private final String _id;
    private final List<PolicyMatchClause> _matchClauses;
    private final List<PolicyValidateClause> _validateClauses;
    private final List<PolicyThrottleClause> _throttleClauses;
    private final PolicyTransformClause _transformClause;
    
    public Policy(
        String id,
        List<PolicyMatchClause> matchClauses,
        List<PolicyValidateClause> validateClauses,
        List<PolicyThrottleClause> throttleClauses,
        PolicyTransformClause transformClause
    ) {
        _id = id;
        _matchClauses = Lists.newArrayList(matchClauses);
        _validateClauses = validateClauses;
        _throttleClauses = Lists.newArrayList(throttleClauses);
        _transformClause = transformClause;
    }

    public String getId() {
        return _id;
    }

    public List<PolicyMatchClause> getMatchClauses() {
        return _matchClauses;
    }

    public List<PolicyValidateClause> getValidateClauses() {
        return _validateClauses;
    }

    public List<PolicyThrottleClause> getThrottleClauses() {
        return _throttleClauses;
    }

    public PolicyTransformClause getTransformClause() {
        return _transformClause;
    }

}
