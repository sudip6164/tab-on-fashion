package com.tabonfashion.controller;

import com.tabonfashion.entity.Cart;
import com.tabonfashion.entity.Order;
import com.tabonfashion.service.AdminService;
import com.tabonfashion.service.CartService;
import com.tabonfashion.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Check if user is logged in
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("user", loggedInUser);
        }
        return "index";
    }
    
    @GetMapping("/shop")
    public String shop() {
        return "redirect:/products";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        
        // Get cart information
        Cart cart = cartService.getCartByUserId(userId);
        int cartItemCount = cart != null ? cart.getTotalItems() : 0;
        
        // Get order information
        List<Order> orders = orderService.getOrdersByUserId(userId);
        int orderCount = orders.size();
        
        model.addAttribute("user", loggedInUser);
        model.addAttribute("cartItemCount", cartItemCount);
        model.addAttribute("orderCount", orderCount);
        return "dashboard";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        Object userRole = session.getAttribute("userRole");
        
        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }
        
        if (userRole == null || !userRole.toString().equals("ADMIN")) {
            return "redirect:/dashboard";
        }
        
        try {
            // Get dashboard statistics
            Map<String, Object> stats = adminService.getDashboardStats();
            
            // Ensure stats is not null
            if (stats == null) {
                stats = new HashMap<>();
                stats.put("totalUsers", 0L);
                stats.put("totalProducts", 0L);
                stats.put("totalOrders", 0L);
                stats.put("totalRevenue", BigDecimal.ZERO);
                stats.put("pendingOrders", 0L);
            }
            
            model.addAttribute("user", loggedInUser);
            model.addAttribute("stats", stats);
            
            // Add individual stats as separate attributes for debugging
            model.addAttribute("totalUsers", stats.get("totalUsers"));
            model.addAttribute("totalProducts", stats.get("totalProducts"));
            model.addAttribute("totalOrders", stats.get("totalOrders"));
            model.addAttribute("totalRevenue", stats.get("totalRevenue"));
            model.addAttribute("pendingOrders", stats.get("pendingOrders"));
            
            return "admin/dashboard";
        } catch (Exception e) {
            // Log the error
            System.err.println("Error loading admin dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to model
            model.addAttribute("errorMessage", "Error loading dashboard data: " + e.getMessage());
            model.addAttribute("user", loggedInUser);
            
            // Add empty stats to prevent null pointer
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalUsers", 0L);
            emptyStats.put("totalProducts", 0L);
            emptyStats.put("totalOrders", 0L);
            emptyStats.put("totalRevenue", BigDecimal.ZERO);
            emptyStats.put("pendingOrders", 0L);
            model.addAttribute("stats", emptyStats);
            
            return "admin/dashboard";
        }
    }
}
