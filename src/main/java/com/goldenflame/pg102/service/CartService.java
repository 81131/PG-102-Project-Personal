package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.CartItem;
import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.ShoppingCart;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.CartItemRepository;
import com.goldenflame.pg102.repository.CatalogueItemRepository;
import com.goldenflame.pg102.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CatalogueItemRepository catalogueItemRepository;

    public CartService(ShoppingCartRepository cartRepository, CartItemRepository cartItemRepository, CatalogueItemRepository catalogueItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.catalogueItemRepository = catalogueItemRepository;
    }

    @Transactional
    public ShoppingCart getCartForUser(User user) {
        return cartRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public void addItemToCart(User user, Long itemId, int quantity) {
        ShoppingCart cart = getCartForUser(user);
        CatalogueItem item = catalogueItemRepository.findById(itemId).orElseThrow();

        // Check if item is already in cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(ci -> ci.getCatalogueItem().getId().equals(itemId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Add new item
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setCatalogueItem(item);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
        }
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}