package goldenflame.singleton;


public class NotificationService {
    private static NotificationService instance;

    private NotificationService() {
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void sendNotification(String message) {
        System.out.println("[NotificationService] " + message);
    }
}
