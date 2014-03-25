package io.benvol.model.auth.fail;

@SuppressWarnings("serial")
public class ThrottleFailure extends RequestRejection {

    // For some reason, the apache httpcomponents project omitted code
    // 429 TOO MANY REQUESTS from their HttpStatus implementation.
    private static final int SC_TOO_MANY_REQUESTS = 429;

    public ThrottleFailure(String message) {
        super(SC_TOO_MANY_REQUESTS, message);
    }

}
