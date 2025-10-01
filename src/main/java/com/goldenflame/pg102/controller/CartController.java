package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.ShoppingCart;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.CartService;
import com.goldenflame.pg102.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public CartController(CartService cartService, UserRepository userRepository, OrderService orderService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return 0;
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        ShoppingCart cart = cartService.getCartForUser(currentUser);
        return cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum();
    }

    @GetMapping
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        ShoppingCart cart = cartService.getCartForUser(currentUser);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long itemId, @RequestParam int quantity, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        cartService.addItemToCart(currentUser, itemId, quantity);
        return "redirect:/item/" + itemId;
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        // 1. Check for delivery person availability
        if (!orderService.isDeliveryPersonAvailable()) {
            redirectAttributes.addFlashAttribute("error", "Sorry, all delivery persons are currently busy. Please try again later to checkout.");
            return "redirect:/cart";
        }

        // 2. Get user and cart details
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        ShoppingCart cart = cartService.getCartForUser(currentUser);

        model.addAttribute("cart", cart);
        model.addAttribute("currentUser", currentUser);
        return "checkout-confirmation";
    }

    @PostMapping("/submit-order")
    public String submitCartOrder(@RequestParam String address,
                                  @RequestParam String phone,
                                  @RequestParam String paymentMethod,
                                  @RequestParam(required = false) String cardNumber,
                                  @RequestParam(required = false) String cvc,
                                  @RequestParam(required = false) Integer expiryMonth,
                                  @RequestParam(required = false) Integer expiryYear,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        ShoppingCart cart = cartService.getCartForUser(currentUser);

        // Validate card if payment method is CARD
        if ("CARD".equals(paymentMethod)) {
            if (cardNumber == null || !orderService.validateCard(cardNumber, cvc, expiryMonth, expiryYear)) {
                redirectAttributes.addFlashAttribute("error", "Invalid card details. Please check and try again.");
                return "redirect:/cart/checkout";
            }
        }

        // Create the order from the cart
        orderService.createOrderFromCart(currentUser, cart, address, phone, paymentMethod);

        return "redirect:/order/success";
    }
}
