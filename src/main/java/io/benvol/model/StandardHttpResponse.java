package io.benvol.model;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

public class StandardHttpResponse {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final StandardHttpRequest _request;
    private final StatusLine _status;
    private final Header[] _headers;
    private final String _responseBody;

    private JsonNode _responseBodyAsJson = null;

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

    public JsonNode getResponseBodyAsJson() {
        if (_responseBodyAsJson == null) {
            try {
                _responseBodyAsJson = OBJECT_MAPPER.readTree(_responseBody);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return _responseBodyAsJson;
    }

}
