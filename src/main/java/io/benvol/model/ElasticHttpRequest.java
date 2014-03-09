package io.benvol.model;

import io.benvol.model.auth.AuthDirective;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.collect.Lists;

public class ElasticHttpRequest extends StandardHttpRequest {

    private final List<String> _indexNames;
    private final List<String> _typeNames;

    private final AuthDirective _authDirective;
    private final ElasticOperator _operator;

    public ElasticHttpRequest(HttpMethod httpMethod, HttpServletRequest request) {
        super(httpMethod, request);

        // Use the HTTP path to determine the index and type names. The first path-part represents
        // one or more index names, while the second path-part represents one or more type names.
        String[] pathParts = super.getPath().split("/");
        _indexNames = parseCommaDelimitedNames(pathParts, 0);
        _typeNames = parseCommaDelimitedNames(pathParts, 1);

        // Build an AuthDirective from the http request headers.
        _authDirective = new AuthDirective(super.getHeaders());

        // Determining the ElasticOperator can be tricky, since it relies on an
        // interplay between the http method, path, and querystring params.
        _operator = ElasticOperator.infer(httpMethod, pathParts, super.getParams());
    }

    public List<String> getIndexNames() {
        return _indexNames;
    }

    public List<String> getTypeNames() {
        return _typeNames;
    }

    public ElasticOperator getElasticOperator() {
        return _operator;
    }

    public AuthDirective getAuthDirective() {
        return _authDirective;
    }

    private static List<String> parseCommaDelimitedNames(String[] pathParts, int index) {
        if (pathParts.length > index) {
            String pathPart = pathParts[index];
            if (!pathPart.startsWith("_")) {
                String[] names = pathPart.split(",");
                if (names.length == 1) {
                    return Collections.singletonList(names[0]);
                } else if (names.length > 1) {
                    return Collections.unmodifiableList(Lists.newArrayList(names));
                }
            }
        }
        return Collections.emptyList();
    }

}
