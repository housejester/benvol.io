package io.benvol.model;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

public class StandardHttpRequest {

    private final HttpKind _httpKind;
    private final String _path;

    private final Map<String, String[]> _params;
    private final Map<String, String[]> _headers;

    private final String _requestBody;

    public StandardHttpRequest(HttpKind httpKind, HttpServletRequest request) {
        this(httpKind, request.getServletPath(), request.getParameterMap(), readHeaders(request), readRequestBody(request));
    }

    public StandardHttpRequest(HttpKind httpKind, String path, Map<String, String[]> params, Map<String, String[]> headers, String requestBody) {
        _httpKind = httpKind;
        _path = path;
        _params = params;
        _headers = headers;
        _requestBody = requestBody;
    }

    public String makeUrl(String hostname, int port) {
        return String.format(
            "https://%s:%s/%s%s",
            hostname, port, _path, getQueryString()
        );
    }

    public HttpKind getHttpKind() {
        return _httpKind;
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

    public String getQueryString() {
        if (_params.isEmpty()) {
            return "";
        } else {
            StringBuilder b = new StringBuilder("?");
            for (Map.Entry<String, String[]> entry : _params.entrySet()) {
                try {
                    String urlEncodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                    for (String value : entry.getValue()) {
                        String urlEncodedValue = URLEncoder.encode(value, "UTF-8");
                        b.append(urlEncodedKey);
                        b.append("=");
                        b.append(urlEncodedValue);
                    }
                } catch (IOException e) {
                    Throwables.propagate(e);
                }
            }
            return b.toString();
        }
    }

    private static String readRequestBody(HttpServletRequest request) {
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
