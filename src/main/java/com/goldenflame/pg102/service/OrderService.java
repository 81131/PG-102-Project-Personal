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
        Payment payment = new Payment();
        payment.setMethod(paymentMethod);
        payment.setAmount(item.getPrice() * quantity);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("COMPLETED");
        Payment savedPayment = paymentRepository.save(payment);

        Order order = new Order();
        order.setUser(user);
        order.setPayment(savedPayment);
        order.setOrderStatus("PREPARING");
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryPhone(deliveryPhone);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setCatalogueItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setPricePerItem(item.getPrice());
        order.setOrderItems(List.of(orderItem));

        Order savedOrder = orderRepository.save(order);

        notificationService.notifyKitchenStaff("New order #" + savedOrder.getId() + " received.", "/kitchen/orders");

        Income income = new Income();
        income.setPayment(savedPayment);
        income.setAmount(savedPayment.getAmount());
        income.setIncomeType("FOOD_ORDER");
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        return savedOrder;
    }

    @Transactional
    public Order createOrderFromCart(User user, ShoppingCart cart, String deliveryAddress, String deliveryPhone, String paymentMethod) {
        // 1. Create Payment
        Payment payment = new Payment();
        payment.setMethod(paymentMethod);
        payment.setAmount(cart.getTotalPrice());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("COMPLETED");
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

        return savedOrder;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Here you can add logic to ensure status moves forward
        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        // Notify the customer
        String message = "Your order #" + order.getId() + " is now " + newStatus.toLowerCase() + ".";
        notificationService.createNotification(order.getUser(), message, "/orders/my-history"); // A future page for customers
    }

    public List<Order> findOrdersForUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
}
