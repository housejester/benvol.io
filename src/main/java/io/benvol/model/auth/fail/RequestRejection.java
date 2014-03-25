package io.benvol.model.auth.fail;

@SuppressWarnings("serial")
public class RequestRejection extends RuntimeException {

    private int _status;

    public RequestRejection(int status, String message) {
        super(message);
        _status = status;
    }

    public int getStatus() {
        return _status;
    }

}
