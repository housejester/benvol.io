package io.benvol.model.auth;

import java.util.List;
import java.util.Map;

public class AuthDirective {
    
    private List<IdentifyPredicate> _identifyPredicates;
    private List<ConfirmPredicate> _confirmPredicates;
    
    public AuthDirective(Map<String, String[]> headers) {
        _identifyPredicates = IdentifyPredicate.fromHeaders(headers);
        _confirmPredicates = ConfirmPredicate.fromHeaders(headers);
    }

    public boolean isAnonymous() {
        return _identifyPredicates.isEmpty();
    }

    public List<IdentifyPredicate> getIdentifyPredicates() {
        return _identifyPredicates;
    }

    public List<ConfirmPredicate> getConfirmPredicates() {
        return _confirmPredicates;
    }
}
