package goldenflame.factory;

public class Payment {
    private String method;
    private float amount;
    private String status;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "method='" + method + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
