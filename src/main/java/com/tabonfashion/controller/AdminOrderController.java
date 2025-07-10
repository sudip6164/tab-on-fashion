package com.tabonfashion.controller;

import com.tabonfashion.entity.Order;
import com.tabonfashion.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public String listOrders(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            HttpSession session) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        
        return "admin/orders/list";
    }
    
    @GetMapping("/pending")
    public String listPendingOrders(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        List<Order> pendingOrders = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);
        model.addAttribute("orders", pendingOrders);
        model.addAttribute("title", "Pending Orders");
        
        return "admin/orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            return "admin/orders/view";
        }
        
        return "redirect:/admin/orders";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                   @RequestParam Order.OrderStatus status,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating order status: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
    
    @PostMapping("/{id}/update-payment")
    public String updatePaymentStatus(@PathVariable Long id,
                                     @RequestParam Order.PaymentStatus paymentStatus,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            orderService.updatePaymentStatus(id, paymentStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Payment status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating payment status: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
    
    private boolean isAdmin(HttpSession session) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null && userRole.toString().equals("ADMIN");
    }
}
