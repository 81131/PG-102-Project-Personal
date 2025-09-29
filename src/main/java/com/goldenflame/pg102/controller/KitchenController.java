package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.Order;
import com.goldenflame.pg102.repository.OrderRepository;
import com.goldenflame.pg102.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public KitchenController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<Order> activeOrders = orderRepository.findByOrderStatusIn(List.of("PREPARING", "PREPARED"));
        model.addAttribute("orders", activeOrders);
        return "kitchen/orders";
    }

    @PostMapping("/orders/update-status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/kitchen/orders";
    }
}