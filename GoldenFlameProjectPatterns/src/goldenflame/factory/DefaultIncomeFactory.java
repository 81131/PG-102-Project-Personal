package goldenflame.factory;

public class DefaultIncomeFactory implements IncomeFactory {
    @Override
    public Income create(Payment payment, String incomeType) {
        Income income = new Income();
        income.setPayment(payment);
        income.setAmount(payment.getAmount());
        income.setIncomeType(incomeType);
        return income;
    }
}
