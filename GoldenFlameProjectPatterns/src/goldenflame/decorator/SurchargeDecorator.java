package goldenflame.decorator;

public class SurchargeDecorator extends PriceDecorator {
    private final float surcharge;

    public SurchargeDecorator(PriceComponent delegate, float surcharge) {
        super(delegate);
        this.surcharge = surcharge;
    }

    @Override
    public float getTotal() {
        return delegate.getTotal() + surcharge;
    }
}
