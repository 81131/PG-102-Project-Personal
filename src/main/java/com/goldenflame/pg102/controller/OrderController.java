package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.CatalogueService;
import com.goldenflame.pg102.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CatalogueService catalogueService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, CatalogueService catalogueService, UserRepository userRepository) {
        this.orderService = orderService;
        this.catalogueService = catalogueService;
        this.userRepository = userRepository;
    }

    @PostMapping("/now")
    public String orderNow(@RequestParam Long itemId,
                           @RequestParam int quantity,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {

        if (!orderService.isDeliveryPersonAvailable()) {
            redirectAttributes.addFlashAttribute("error", "Sorry, all our delivery persons are currently busy. Please try again later.");
            return "redirect:/item/" + itemId;
        }

        Optional<CatalogueItem> itemOptional = catalogueService.findById(itemId);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "The selected item could not be found.");
            return "redirect:/menu";
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("item", itemOptional.get());
        model.addAttribute("quantity", quantity);
        model.addAttribute("currentUser", currentUser);
        return "order-confirm";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam Long itemId,
                              @RequestParam int quantity,
                              @RequestParam String address,
                              @RequestParam String phone,
                              @RequestParam String paymentMethod,
                              @RequestParam(required = false) String cardNumber,
                              @RequestParam(required = false) String cvc,
                              @RequestParam(required = false) Integer expiryMonth,
                              @RequestParam(required = false) Integer expiryYear,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {

        if ("CARD".equals(paymentMethod)) {
            if (cardNumber == null || cvc == null || expiryMonth == null || expiryYear == null ||
                    !orderService.validateCard(cardNumber, cvc, expiryMonth, expiryYear)) {

                redirectAttributes.addFlashAttribute("error", "Invalid card details. Please check and try again.");
                return "redirect:/item/" + itemId;
            }
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        CatalogueItem item = catalogueService.findById(itemId).get();

        orderService.createOrder(currentUser, item, quantity, address, phone, paymentMethod);

        return "redirect:/order/success";
    }
}