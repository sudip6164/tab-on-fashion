package com.tabonfashion.service;

import com.tabonfashion.entity.Cart;
import com.tabonfashion.entity.CartItem;
import com.tabonfashion.entity.Product;
import com.tabonfashion.entity.User;
import com.tabonfashion.repository.CartItemRepository;
import com.tabonfashion.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    public Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        Cart newCart = new Cart(user);
        return cartRepository.save(newCart);
    }
    
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }
    
    public CartItem addToCart(User user, Product product, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        
        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            updateCartTotal(cart);
            return item;
        } else {
            // Create new cart item
            CartItem newItem = new CartItem(cart, product, quantity, product.getPrice());
            cartItemRepository.save(newItem);
            updateCartTotal(cart);
            return newItem;
        }
    }
    
    public void removeFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.findByCart(cart).stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item -> {
                cartItemRepository.delete(item);
                updateCartTotal(cart);
            });
    }
    
    public void updateCartItemQuantity(User user, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.findByCart(cart).stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item -> {
                if (quantity <= 0) {
                    cartItemRepository.delete(item);
                } else {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                }
                updateCartTotal(cart);
            });
    }
    
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart(cart);
        cart.setTotalAmount(java.math.BigDecimal.ZERO);
        cartRepository.save(cart);
    }
    
    private void updateCartTotal(Cart cart) {
        cart.calculateTotalAmount();
        cartRepository.save(cart);
    }
}
