package goldenflame.observer;

import goldenflame.singleton.NotificationService;

public class NotificationListener implements EventListener {
    @Override
    public void onEvent(Event event) {
        NotificationService service = NotificationService.getInstance();
        if (event instanceof OrderCreatedEvent) {
            String msg = ((OrderCreatedEvent) event).getMessage();
            service.sendNotification(msg);
        } else if (event instanceof BookingApprovedEvent) {
            String msg = ((BookingApprovedEvent) event).getMessage();
            service.sendNotification(msg);
        } else {
            service.sendNotification("Received event: " + event);
        }
    }
}
