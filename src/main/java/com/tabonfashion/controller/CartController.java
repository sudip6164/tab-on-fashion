package com.tabonfashion.controller;

import com.tabonfashion.entity.Cart;
import com.tabonfashion.entity.Product;
import com.tabonfashion.entity.User;
import com.tabonfashion.service.CartService;
import com.tabonfashion.service.ProductService;
import com.tabonfashion.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String showCart(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        Cart cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        return "cart/view";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            Optional<Product> productOpt = productService.getProductById(productId);
            
            if (userOpt.isPresent() && productOpt.isPresent()) {
                cartService.addToCart(userOpt.get(), productOpt.get(), quantity);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Product added to cart successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Product not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error adding product to cart: " + e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                cartService.removeFromCart(userOpt.get(), productId);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Product removed from cart!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error removing product from cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long productId,
                                @RequestParam Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                cartService.updateCartItemQuantity(userOpt.get(), productId, quantity);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Cart updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                cartService.clearCart(userOpt.get());
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Cart cleared successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error clearing cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
}
