package io.benvol.model;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class StandardHttpResponse {

    private final StandardHttpRequest _request;
    private final StatusLine _status;
    private final Header[] _headers;
    private final String _responseBody;

    public StandardHttpResponse(
        StandardHttpRequest request,
        StatusLine status,
        Header[] headers,
        String responseBody
    ) {
        _request = request;
        _status = status;
        _headers = headers;
        _responseBody = responseBody;
    }

    public StandardHttpRequest getRequest() {
        return _request;
    }

    public StatusLine getStatus() {
        return _status;
    }

    public Header[] getHeaders() {
        return _headers;
    }

    public String getResponseBody() {
        return _responseBody;
    }

}
