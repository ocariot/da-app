package br.edu.uepb.nutes.ocariot.utils;

public class MessageEvent {
    private String name;
    private Object message;

    public MessageEvent(String name) {
        this.name = name;
    }

    public MessageEvent(String name, Object message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "name='" + name + '\'' +
                ", message=" + message +
                '}';
    }

    public static class EventType {
        public static String OCARIOT_ACCESS_TOKEN_EXPIRED = "ocariot_access_token_expired_event";
        public static String FITBIT_ACCESS_TOKEN_EXPIRED = "fitbit_access_token_expired_event";
        public static String FITBIT_NO_ACCESS_TOKEN = "fitbit_no_access_token_event";
    }
}
