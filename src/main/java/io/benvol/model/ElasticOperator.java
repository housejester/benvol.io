package io.benvol.model;

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
}
