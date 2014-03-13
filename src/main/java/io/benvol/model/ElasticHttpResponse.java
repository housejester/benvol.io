package io.benvol.model;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class ElasticHttpResponse extends StandardHttpResponse {

    public ElasticHttpResponse(
        ElasticHttpRequest request,
        StatusLine status,
        Header[] headers,
        String responseBody
    ) {
        super(request, status, headers, responseBody);
    }

    @Override
    public ElasticHttpRequest getRequest() {
        return (ElasticHttpRequest) super.getRequest();
    }

}
