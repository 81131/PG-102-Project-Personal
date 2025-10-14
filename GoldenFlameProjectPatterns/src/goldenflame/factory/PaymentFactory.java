package goldenflame.factory;

public interface PaymentFactory {
    Payment create(String method, float amount);
}
