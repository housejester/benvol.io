package io.benvol.model.auth;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class IdentifyPredicate {

    private static final String HEADER_PREFIX = "auth.identify.";

    private final String _userType;
    private final String _qualifiedField;
    private final String _operand;

    public IdentifyPredicate(String userType, String qualifiedField, String operand) {
        _userType = userType;
        _qualifiedField = qualifiedField;
        _operand = operand;
    }

    public IdentifyPredicate(String headerName, String headerValue) {
        String field = headerName.substring(HEADER_PREFIX.length() + 1);
        int dotPosition = field.indexOf('.');
        _userType = field.substring(0, dotPosition);
        _qualifiedField = field.substring(dotPosition + 1);
        _operand = headerValue;
    }

    public static List<IdentifyPredicate> fromHeaders(Map<String, String[]> headers) {
        List<IdentifyPredicate> predicates = Lists.newArrayList();
        for (String headerName : headers.keySet()) {
            if (headerName.startsWith(HEADER_PREFIX)) {
                for (String headerValue : headers.get(headerName)) {
                    predicates.add(new IdentifyPredicate(headerName, headerValue));
                }
            }
        }
        return predicates;
    }

    public String getUserType() {
        return _userType;
    }

    public String getQualifiedField() {
        return _qualifiedField;
    }

    public String getOperand() {
        return _operand;
    }

}