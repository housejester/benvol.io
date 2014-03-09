package io.benvol.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.collect.Lists;

public class ElasticHttpRequest extends StandardHttpRequest {
    
    private final List<String> _indexNames;
    private final List<String> _typeNames;
    
    private final ElasticOperator _operator;
    
    public ElasticHttpRequest(HttpMethod httpMethod, HttpServletRequest request) {
        super(httpMethod, request);

        // Use the HTTP path to determine the index and type names. The first path-part represents
        // one or more index names, while the second path-part represents one or more type names.
        String[] pathParts = super.getPath().split("/");
        _indexNames = parseCommaDelimitedNames(pathParts, 0);
        _typeNames = parseCommaDelimitedNames(pathParts, 1);
        
        // Determining the ElasticOperator can be tricky, since it relies on an
        // interplay between the http method, path, and querystring params.
        _operator = getElasticOperator(httpMethod, pathParts, super.getParams());
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

    private static ElasticOperator getElasticOperator(HttpMethod httpMethod, String[] pathParts, Map<String, String[]> params) {
        for (String pathPart : pathParts) {
            if (pathPart.startsWith("_")) {
                if (pathPart.equals("_mapping")) {
                    if (HttpMethod.PUT.equals(httpMethod) || HttpMethod.POST.equals(httpMethod)) {
                        return ElasticOperator.MAPPING_PUT;
                    } else if (HttpMethod.GET.equals(httpMethod)) {
                        return ElasticOperator.MAPPING_GET;
                    } else if (HttpMethod.DELETE.equals(httpMethod)) {
                        return ElasticOperator.MAPPING_DELETE;
                    }
                }
                return ElasticOperator.parse(pathPart);
            }
        }
        if (HttpMethod.DELETE.equals(httpMethod)) {
            return ElasticOperator.DELETE;
        }
        throw new RuntimeException("INTERNAL ERROR: can't determine the ElasticOperation for query");
    }
    
}
