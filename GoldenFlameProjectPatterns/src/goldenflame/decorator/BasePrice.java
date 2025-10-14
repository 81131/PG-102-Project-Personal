package goldenflame.decorator;


public class BasePrice implements PriceComponent {
    private final float amount;

    public BasePrice(float amount) {
        this.amount = amount;
    }

    @Override
    public float getTotal() {
        return amount;
    }
}
