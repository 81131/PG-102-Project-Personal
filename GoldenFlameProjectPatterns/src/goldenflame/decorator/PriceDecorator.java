package goldenflame.decorator;

public abstract class PriceDecorator implements PriceComponent {
    protected final PriceComponent delegate;

    protected PriceDecorator(PriceComponent delegate) {
        this.delegate = delegate;
    }
}
