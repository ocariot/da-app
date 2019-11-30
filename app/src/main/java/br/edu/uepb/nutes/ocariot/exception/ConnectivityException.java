package br.edu.uepb.nutes.ocariot.exception;

import java.io.IOException;

public class ConnectivityException extends IOException {

    public ConnectivityException() {
    }

    public ConnectivityException(String message) {
        super(message);
    }

    public ConnectivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
