package com.tabonfashion.controller;

import com.tabonfashion.entity.Cart;
import com.tabonfashion.entity.Order;
import com.tabonfashion.entity.User;
import com.tabonfashion.service.CartService;
import com.tabonfashion.service.OrderService;
import com.tabonfashion.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public String showOrders(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        List<Order> orders = orderService.getOrdersByUserId(userId);
        model.addAttribute("orders", orders);
        return "orders/list";
    }
    
    @GetMapping("/{id}")
    public String showOrderDetails(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Check if order belongs to current user
            if (order.getUser().getId().equals(userId)) {
                model.addAttribute("order", order);
                return "orders/details";
            }
        }
        
        return "redirect:/orders";
    }
    
    @GetMapping("/checkout")
    public String showCheckout(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        // Get user's cart
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return "redirect:/cart?error=empty";
        }
        
        // Calculate totals
        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal shipping = BigDecimal.ZERO; // Free shipping
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.08)); // 8% tax
        BigDecimal total = subtotal.add(shipping).add(tax);
        
        // Add cart and totals to model
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        model.addAttribute("itemCount", cart.getCartItems().size());
        
        return "orders/checkout";
    }
    
    @PostMapping("/place")
    public String placeOrder(@RequestParam String shippingAddress,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                Order order = orderService.createOrderFromCart(userOpt.get(), shippingAddress);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Order placed successfully! Order ID: " + order.getId());
                return "redirect:/orders/" + order.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error placing order: " + e.getMessage());
            return "redirect:/cart";
        }
        
        return "redirect:/orders";
    }
}
