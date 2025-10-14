package goldenflame.observer;

public class OrderCreatedEvent implements Event {
    private final String message;

    public OrderCreatedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
