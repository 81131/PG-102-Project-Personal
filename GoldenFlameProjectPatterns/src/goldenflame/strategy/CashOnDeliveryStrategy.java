package goldenflame.strategy;

public class CashOnDeliveryStrategy implements PaymentStrategy {
    @Override
    public void pay(float amount) {
        System.out.println("[CashOnDeliveryStrategy] Paid " + amount + " in cash on delivery");
    }
}
