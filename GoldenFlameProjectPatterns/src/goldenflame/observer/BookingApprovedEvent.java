package goldenflame.observer;

public class BookingApprovedEvent implements Event {
    private final String message;

    public BookingApprovedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
