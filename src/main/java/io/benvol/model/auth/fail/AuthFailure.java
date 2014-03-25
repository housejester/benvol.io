package io.benvol.model.auth.fail;

import org.apache.http.HttpStatus;

@SuppressWarnings("serial")
public class AuthFailure extends RequestRejection {

    public AuthFailure(String message) {
        super(HttpStatus.SC_UNAUTHORIZED, message);
    }

}
