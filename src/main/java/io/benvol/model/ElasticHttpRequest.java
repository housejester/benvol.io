package io.benvol.model;

import io.benvol.model.auth.AuthDirective;
import io.benvol.util.JSON;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class ElasticHttpRequest extends StandardHttpRequest {

    private final List<String> _indexNames;
    private final List<String> _typeNames;

    private final AuthDirective _authDirective;
    private final ElasticOperator _operator;

    public ElasticHttpRequest(HttpKind httpKind, HttpServletRequest request) {
        super(httpKind, request);

        // Use the HTTP path to determine the index and type names. The first path-part represents
        // one or more index names, while the second path-part represents one or more type names.
        String[] pathParts = parsePathIntoParts(super.getPath());
        _indexNames = parseCommaDelimitedNames(pathParts, 0);
        _typeNames = parseCommaDelimitedNames(pathParts, 1);

        // Build an AuthDirective from the http request headers.
        _authDirective = new AuthDirective(super.getHeaders());

        // Determining the ElasticOperator can be tricky, since it relies on an
        // interplay between the http kind, path, and querystring params.
        _operator = ElasticOperator.infer(getHttpKind(), pathParts, super.getParams());
    }

    public ElasticHttpRequest(HttpKind httpKind, String path, Map<String, String[]> params, Map<String, String[]> headers, String requestBody) {
        super(httpKind, path, params, headers, requestBody);

        // Use the HTTP path to determine the index and type names. The first path-part represents
        // one or more index names, while the second path-part represents one or more type names.
        String[] pathParts = parsePathIntoParts(super.getPath());
        _indexNames = parseCommaDelimitedNames(pathParts, 0);
        _typeNames = parseCommaDelimitedNames(pathParts, 1);

        // Build an AuthDirective from the http request headers.
        _authDirective = new AuthDirective(super.getHeaders());

        // Determining the ElasticOperator can be tricky, since it relies on an
        // interplay between the http kind, path, and querystring params.
        _operator = ElasticOperator.infer(getHttpKind(), pathParts, super.getParams());
    }

    public ElasticHttpRequest(HttpKind httpKind, String path, String requestBody) {
        this(
            httpKind,
            path,
            Collections.<String, String[]>emptyMap(),
            Collections.<String, String[]>emptyMap(),
            requestBody
        );
    }

    public ElasticHttpRequest(HttpKind httpKind, String path, JsonNode json) {
        this(
            httpKind,
            path,
            Collections.<String, String[]>emptyMap(),
            Collections.<String, String[]>emptyMap(),
            JSON.stringify(json)
        );
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

    private static String[] parsePathIntoParts(String path) {
        if (path == null) {
            return new String[0];
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            return new String[0];
        }
        return path.split("/");
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
