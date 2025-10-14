package goldenflame.factory;


public class DefaultPaymentFactory implements PaymentFactory {
    @Override
    public Payment create(String method, float amount) {
        Payment payment = new Payment();
        payment.setMethod(method);
        payment.setAmount(amount);
        if ("CASH_ON_DELIVERY".equalsIgnoreCase(method)) {
            payment.setStatus("PENDING");
        } else {
            payment.setStatus("COMPLETED");
        }
        return payment;
    }
}
