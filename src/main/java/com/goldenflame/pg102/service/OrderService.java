package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.*;
import com.goldenflame.pg102.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.goldenflame.pg102.model.ShoppingCart;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final IncomeRepository incomeRepository;
    private final CartService cartService;
    private final NotificationService notificationService;


    public OrderService(UserRepository userRepository, OrderRepository orderRepository, CardRepository cardRepository, PaymentRepository paymentRepository, IncomeRepository incomeRepository, CartService cartService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.incomeRepository = incomeRepository;
        this.cartService = cartService;
        this.notificationService = notificationService;
    }

    public boolean isDeliveryPersonAvailable() {
        long deliveryPersonCount = userRepository.countByRole_Name("ROLE_DELIVERY_PERSON");
        if (deliveryPersonCount == 0) {
            return false;
        }
        long activeOrders = orderRepository.countByOrderStatusIn(
                List.of("PREPARING", "PREPARED", "OUT_FOR_DELIVERY")
        );
        return deliveryPersonCount > activeOrders;
    }

    private User findAvailableDeliveryPerson() {
        // Find all users who are delivery persons
        List<User> allDeliveryPersons = userRepository.findByRole_Name("ROLE_DELIVERY_PERSON");
        // Find all orders that are currently active
        List<Order> activeOrders = orderRepository.findByOrderStatusIn(List.of("PREPARING", "PREPARED", "OUT_FOR_DELIVERY"));

        // Get the IDs of delivery persons who are currently busy
        List<Long> busyPersonIds = activeOrders.stream()
                .filter(order -> order.getDeliveryPerson() != null) // Ensure delivery person is not null
                .map(order -> order.getDeliveryPerson().getId())
                .collect(Collectors.toList());

        // Find the first delivery person who is NOT in the busy list
        return allDeliveryPersons.stream()
                .filter(person -> !busyPersonIds.contains(person.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available delivery person found. This should have been checked earlier."));
    }

    public boolean validateCard(String cardNumber, String cvc, int expiryMonth, int expiryYear) {
        return cardRepository.findByCardNumberAndCvcAndExpiryMonthAndExpiryYear(
                        cardNumber, cvc, expiryMonth, expiryYear)
                .isPresent();
    }


    @Transactional
    public Order createOrder(User user, CatalogueItem item, int quantity, String deliveryAddress, String deliveryPhone, String paymentMethod) {
        // 1. Find an available delivery person
        User assignedDeliveryPerson = findAvailableDeliveryPerson();

        // 2. Create and save the Payment record
        Payment payment = new Payment();
        payment.setMethod(paymentMethod);
        payment.setAmount(item.getPrice() * quantity);
        payment.setPaymentDate(LocalDateTime.now());
        if ("CASH_ON_DELIVERY".equals(paymentMethod)) {
            payment.setStatus("PENDING");
        } else {
            payment.setStatus("COMPLETED");
        }
        Payment savedPayment = paymentRepository.save(payment);

        // 3. Create the Order
        Order order = new Order();
        order.setUser(user);
        order.setPayment(savedPayment);
        order.setDeliveryPerson(assignedDeliveryPerson);
        order.setOrderStatus("PREPARING");
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryPhone(deliveryPhone);

        // 4. Create the OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setCatalogueItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setPricePerItem(item.getPrice());
        order.setOrderItems(List.of(orderItem));

        // 5. Save the Order
        Order savedOrder = orderRepository.save(order);

        // 6. Create Income Record
        Income income = new Income();
        income.setPayment(savedPayment);
        income.setAmount(savedPayment.getAmount());
        income.setIncomeType("FOOD_ORDER");
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        // 7. Notify Kitchen Staff
        notificationService.notifyKitchenStaff("New order #" + savedOrder.getId() + " received.", "/kitchen/orders");

        return savedOrder;
    }

    @Transactional
    public Order createOrderFromCart(User user, ShoppingCart cart, String deliveryAddress, String deliveryPhone, String paymentMethod) {
        // 1. Create Payment
        Payment payment = new Payment();
        payment.setMethod(paymentMethod);

        // Calculate the total amount from the cart, not a single item
        payment.setAmount(cart.getTotalPrice());

        payment.setPaymentDate(LocalDateTime.now());
        if ("CASH_ON_DELIVERY".equals(paymentMethod)) {
            payment.setStatus("PENDING");
        } else {
            payment.setStatus("COMPLETED");
        }
        Payment savedPayment = paymentRepository.save(payment);

        // 2. Create Order
        Order order = new Order();
        order.setUser(user);
        order.setPayment(savedPayment);
        order.setDeliveryPerson(findAvailableDeliveryPerson());
        order.setOrderStatus("PREPARING");
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryPhone(deliveryPhone);

        // 3. Convert CartItems to OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCatalogueItem(cartItem.getCatalogueItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerItem(cartItem.getCatalogueItem().getPrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // 4. Create Income Record
        Income income = new Income();
        income.setPayment(savedPayment);
        income.setAmount(savedPayment.getAmount());
        income.setIncomeType("FOOD_ORDER");
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        // 5. Clear the user's shopping cart
        cartService.clearCart(user);

        // 6. Notify Kitchen Staff
        notificationService.notifyKitchenStaff("New order #" + savedOrder.getId() + " from cart received.", "/kitchen/orders");

        return savedOrder;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Here you can add logic to ensure status moves forward
        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        if ("OUT_FOR_DELIVERY".equals(newStatus)) {
            User deliveryPerson = order.getDeliveryPerson();
            if (deliveryPerson != null) {
                String message = "New delivery task: Order #" + order.getId() + ". Address: " + order.getDeliveryAddress();
                notificationService.createNotification(deliveryPerson, message, "/delivery/my-tasks");
            }
        }

        // Notify the customer
        String message = "Your order #" + order.getId() + " is now " + newStatus.toLowerCase() + ".";
        notificationService.createNotification(order.getUser(), message, "/orders/my-history"); // A future page for customers

    }

    public List<Order> findOrdersForUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }


    @Transactional
    public void completeOrderDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("COMPLETED");

        // If it was a COD order, update the payment status now
        if ("CASH_ON_DELIVERY".equals(order.getPayment().getMethod())) {
            Payment payment = order.getPayment();
            payment.setStatus("COMPLETED");
            paymentRepository.save(payment);
        }

        orderRepository.save(order);
        notificationService.createNotification(order.getUser(), "Your order #" + order.getId() + " has been delivered!", "/orders/my-history");
    }

    @Transactional
    public void cancelOrderDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        // Safety check: only allow cancellation for COD
        if ("CASH_ON_DELIVERY".equals(order.getPayment().getMethod())) {
            order.setOrderStatus("CANCELLED");
            orderRepository.save(order);

            Payment payment = order.getPayment();
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            notificationService.createNotification(order.getUser(), "Your order #" + order.getId() + " was cancelled during delivery.", "/orders/my-history");
        }

    }
}
