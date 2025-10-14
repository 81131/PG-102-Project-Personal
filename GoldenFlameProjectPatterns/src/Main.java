import goldenflame.singleton.NotificationService;
import goldenflame.factory.*;
import goldenflame.decorator.PricingEngine;
import goldenflame.strategy.*;
import goldenflame.observer.*;


public class Main {
    public static void main(String[] args) {
        // Singleton: get the NotificationService instance
        System.out.println("------------------------------------------------------------------");
        System.out.println("Singleton");
        NotificationService service1 = NotificationService.getInstance();
        NotificationService service2 = NotificationService.getInstance();
        System.out.println("Singleton returns same instance: " + (service1 == service2));
        service1.sendNotification("System initialized.");
        System.out.println("------------------------------------------------------------------");
        System.out.println();

        // Factory: create payment and income entries
        System.out.println("------------------------------------------------------------------");
        System.out.println("Factory");
        PaymentFactory paymentFactory = new DefaultPaymentFactory();
        IncomeFactory incomeFactory = new DefaultIncomeFactory();
        Payment payment = paymentFactory.create("CASH_ON_DELIVERY", 500.0f);
        Income income = incomeFactory.create(payment, "FOOD_ORDER");
        System.out.println(payment);
        System.out.println(income);
        System.out.println("------------------------------------------------------------------");
        System.out.println();

        // Decorator: calculate event total using PricingEngine (no discount or tax)
        System.out.println("------------------------------------------------------------------");
        System.out.println("Decorator");
        PricingEngine pricingEngine = new PricingEngine();
        float total = pricingEngine.calculateTotal(20.0f, 60);
        System.out.println("Total event price: " + total);
        System.out.println("------------------------------------------------------------------");
        System.out.println();

        // Strategy: process payments using different algorithms at runtime
        System.out.println("------------------------------------------------------------------");
        System.out.println("Strategy");
        PaymentContext paymentContext = new PaymentContext(new CardPaymentStrategy());
        paymentContext.pay(200.0f);
        paymentContext.setStrategy(new CashOnDeliveryStrategy());
        paymentContext.pay(300.0f);
        System.out.println("------------------------------------------------------------------");
        System.out.println();

        // Observer: publish domain events and notify listeners
        System.out.println("------------------------------------------------------------------");
        System.out.println("Observer");
        DomainEventsPublisher publisher = new DomainEventsPublisher();
        publisher.registerListener(new NotificationListener());
        publisher.publishEvent(new OrderCreatedEvent("Order #101 has been created."));
        publisher.publishEvent(new BookingApprovedEvent("Your event booking for 25th Oct has been approved."));
        System.out.println("------------------------------------------------------------------");
        System.out.println();
    }
}
