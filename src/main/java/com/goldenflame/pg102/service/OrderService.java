package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.*;
import com.goldenflame.pg102.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final IncomeRepository incomeRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, CardRepository cardRepository, PaymentRepository paymentRepository, IncomeRepository incomeRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.incomeRepository = incomeRepository;
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

        Income income = new Income();
        income.setPayment(savedPayment);
        income.setAmount(savedPayment.getAmount());
        income.setIncomeType("FOOD_ORDER");
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        return savedOrder;
    }
}