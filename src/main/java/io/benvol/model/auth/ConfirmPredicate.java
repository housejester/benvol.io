package io.benvol.model.auth;

import java.util.List;
import java.util.Map;

import org.elasticsearch.common.collect.Lists;

public class ConfirmPredicate {
    
    private static final String HEADER_PREFIX = "auth.confirm:";
    
    private final ConfirmKind _confirmKind;
    private final String _operand;
    
    public ConfirmPredicate(ConfirmKind confirmKind, String operand) {
        _confirmKind = confirmKind;
        _operand = operand;
    }
    
    public ConfirmPredicate(String headerName, String headerValue) {
        String suffix = headerName.substring(HEADER_PREFIX.length() + 1);
        _confirmKind = ConfirmKind.valueOf(suffix);
        _operand = headerValue;
    }

    public static List<ConfirmPredicate> fromHeaders(Map<String, String[]> headers) {
        List<ConfirmPredicate> predicates = Lists.newArrayList();
        for (String headerName : headers.keySet()) {
            if (headerName.startsWith(HEADER_PREFIX)) {
                for (String headerValue : headers.get(headerName)) {
                    predicates.add(new ConfirmPredicate(headerName, headerValue));
                }
            }
        }
        return predicates;
    }
    
    public ConfirmKind getConfirmKind() {
        return _confirmKind;
    }
    
    public String getOperand() {
        return _operand;
    }
    
}