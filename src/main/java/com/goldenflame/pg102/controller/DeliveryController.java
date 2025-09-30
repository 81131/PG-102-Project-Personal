package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.Order;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.OrderRepository;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public DeliveryController(OrderRepository orderRepository, OrderService orderService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-tasks")
    public String showMyTasks(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        List<Order> assignedOrders = orderRepository.findByDeliveryPersonAndOrderStatus(currentUser, "OUT_FOR_DELIVERY");
        model.addAttribute("orders", assignedOrders);
        return "delivery/my-tasks";
    }

    @PostMapping("/complete")
    public String completeTask(@RequestParam Long orderId) {
        orderService.completeOrderDelivery(orderId);
        return "redirect:/delivery/my-tasks";
    }

    @PostMapping("/cancel")
    public String cancelTask(@RequestParam Long orderId) {
        orderService.cancelOrderDelivery(orderId);
        return "redirect:/delivery/my-tasks";
    }
}