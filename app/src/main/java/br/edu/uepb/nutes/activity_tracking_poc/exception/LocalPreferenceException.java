package br.edu.uepb.nutes.activity_tracking_poc.exception;

public class LocalPreferenceException extends Exception {
    public LocalPreferenceException() {
    }

    public LocalPreferenceException(String message) {
        super(message);
    }

    public LocalPreferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
