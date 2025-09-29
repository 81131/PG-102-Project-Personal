package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.ShoppingCart;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.UserRepository;
import com.goldenflame.pg102.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // This makes the cart item count available to all templates
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
        return "cart"; // New cart view template
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
}