package goldenflame.strategy;

public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(float amount) {
        System.out.println("[CardPaymentStrategy] Paid " + amount + " using card");
    }
}
