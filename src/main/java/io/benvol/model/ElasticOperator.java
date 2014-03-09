package io.benvol.model;

import java.util.Map;

import com.google.common.base.Preconditions;

public enum ElasticOperator {

    // FROM THE DOCUMENT API: http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs.html
    INDEX,
    GET,
    DELETE,
    UPDATE,
    MGET,
    BULK,
    TERMVECTOR,
    MTERMVECTOR,

    // FROM THE SEARCH API: http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search.html
    SEARCH,
    COUNT,
    MSEARCH,
    VALIDATE,
    EXPLAIN,
    PERCOLATE,
    MLT,

    // META-API STUFF
    MAPPING_GET,
    MAPPING_PUT,
    MAPPING_DELETE
    ;

    public static ElasticOperator parse(String string) {
        Preconditions.checkNotNull(string, "cannot parse ElasticOperator from null string");
        if (string.startsWith("_")) {
            string = string.substring(1);
        }
        string = string.toUpperCase();
        return valueOf(string);
    }

    public static ElasticOperator infer(HttpMethod httpMethod, String[] pathParts, Map<String, String[]> params) {
        for (String pathPart : pathParts) {
            if (pathPart.startsWith("_")) {
                if (pathPart.equals("_mapping")) {
                    if (HttpMethod.PUT.equals(httpMethod) || HttpMethod.POST.equals(httpMethod)) {
                        return MAPPING_PUT;
                    } else if (HttpMethod.GET.equals(httpMethod)) {
                        return MAPPING_GET;
                    } else if (HttpMethod.DELETE.equals(httpMethod)) {
                        return MAPPING_DELETE;
                    }
                }
                return parse(pathPart);
            }
        }
        if (HttpMethod.DELETE.equals(httpMethod)) {
            return DELETE;
        }
        throw new RuntimeException("INTERNAL ERROR: can't determine the ElasticOperation for query");
    }
}
