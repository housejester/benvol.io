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

    public static ElasticOperator infer(HttpKind httpKind, String[] pathParts, Map<String, String[]> params) {
        for (String pathPart : pathParts) {
            if (pathPart.startsWith("_")) {
                if (pathPart.equals("_mapping")) {
                    if (HttpKind.PUT.equals(httpKind) || HttpKind.POST.equals(httpKind)) {
                        return MAPPING_PUT;
                    } else if (HttpKind.GET.equals(httpKind)) {
                        return MAPPING_GET;
                    } else if (HttpKind.DELETE.equals(httpKind)) {
                        return MAPPING_DELETE;
                    }
                }
                return parse(pathPart);
            }
        }
        if (HttpKind.DELETE.equals(httpKind)) {
            return DELETE;
        }
        throw new RuntimeException("INTERNAL ERROR: can't determine the ElasticOperation for query");
    }
}
