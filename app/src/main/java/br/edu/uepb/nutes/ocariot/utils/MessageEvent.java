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

    public final class EventType {
        private EventType() {
            throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
        }

        public static final String OCARIOT_ACCESS_TOKEN_EXPIRED = "ocariot_access_token_expired_event";
    }
}
