package io.benvol.model.auth.fail;

import org.apache.http.HttpStatus;

@SuppressWarnings("serial")
public class PolicyRejection extends RequestRejection {

    public PolicyRejection(String message) {
        super(HttpStatus.SC_CONFLICT, message);
    }

}
