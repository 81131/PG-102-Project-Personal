package goldenflame.factory;


public class Income {
    private Payment payment;
    private float amount;
    private String incomeType;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    @Override
    public String toString() {
        return "Income{" +
                "payment=" + payment +
                ", amount=" + amount +
                ", incomeType='" + incomeType + '\'' +
                '}';
    }
}
