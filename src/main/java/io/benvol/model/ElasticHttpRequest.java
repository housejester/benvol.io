package io.benvol.model;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.collect.Lists;

public class ElasticHttpRequest extends StandardHttpRequest {
    
    private final List<String> _indexNames;
    private final List<String> _typeNames;
    
    public ElasticHttpRequest(HttpMethod httpMethod, HttpServletRequest request) {
        super(httpMethod, request);

        // Use the HTTP path to determine the index and type names. The first path-part represents
        // one or more index names, while the second path-part represents one or more type names.
        String[] pathParts = super.getPath().split("/");
        _indexNames = parseCommaDelimitedNames(pathParts, 0);
        _typeNames = parseCommaDelimitedNames(pathParts, 1);
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
