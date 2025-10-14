package goldenflame.factory;

public interface IncomeFactory {
    Income create(Payment payment, String incomeType);
}
