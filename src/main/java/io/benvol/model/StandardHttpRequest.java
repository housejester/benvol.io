package io.benvol.model;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.base.Throwables;

import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

public class StandardHttpRequest {
    
    private final HttpMethod _httpMethod;
    private final String _path;

    private final Map<String, String[]> _params;
    private final Map<String, String[]> _headers;

    private final String _requestBody;

    public StandardHttpRequest(HttpMethod httpMethod, HttpServletRequest request) {
        _httpMethod = httpMethod;
        _path = request.getPathInfo();
        
        _params = request.getParameterMap();
        _headers = readHeaders(request);
        _requestBody = readRequestBody(request);
    }
    
    public HttpMethod getHttpMethod() {
        return _httpMethod;
    }
    
    public String getPath() {
        return _path;
    }
    
    public Map<String, String[]> getParams() {
        return _params;
    }
    
    public Map<String, String[]> getHeaders() {
        return _headers;
    }
    
    public String getRequestBody() {
        return _requestBody;
    }

    private String readRequestBody(HttpServletRequest request) {
        String requestBody = "";
        if (request.getContentLength() > 0) {
            try {
                requestBody = CharStreams.toString(request.getReader());
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return requestBody;
    }

    private static Map<String, String[]> readHeaders(HttpServletRequest request) {
        Map<String, String[]> headers = Maps.newHashMap();
        for (String headerName : Collections.list(request.getHeaderNames())) {
            List<String> headersForName = Collections.list(request.getHeaders(headerName));
            String[] headerArrayForName = headersForName.toArray(new String[headersForName.size()]);
            headers.put(headerName, headerArrayForName);
        }
        return headers;
    }

}
