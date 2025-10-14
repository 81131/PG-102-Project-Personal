package goldenflame.decorator;


public class PricingEngine {

    public float calculateTotal(float perGuestPrice, int guests) {
        float baseAmount = perGuestPrice * guests;
        PriceComponent component = new BasePrice(baseAmount);
        // Add a surcharge if the number of guests exceeds the threshold
        if (guests > 50) {
            component = new SurchargeDecorator(component, perGuestPrice);
        }
        return component.getTotal();
    }
}
