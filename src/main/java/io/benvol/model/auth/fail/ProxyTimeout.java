package io.benvol.model.auth.fail;

import org.apache.http.HttpStatus;

@SuppressWarnings("serial")
public class ProxyTimeout extends RequestRejection {

    public ProxyTimeout(String message) {
        super(HttpStatus.SC_GATEWAY_TIMEOUT, message);
    }

}
