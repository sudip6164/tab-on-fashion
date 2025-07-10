package com.tabonfashion.service;

import com.tabonfashion.entity.Order;
import com.tabonfashion.repository.OrderRepository;
import com.tabonfashion.repository.ProductRepository;
import com.tabonfashion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total users count
            long totalUsers = userRepository.count();
            stats.put("totalUsers", totalUsers);
            
            // Get total products count
            long totalProducts = productRepository.count();
            stats.put("totalProducts", totalProducts);
            
            // Get total orders count
            long totalOrders = orderRepository.count();
            stats.put("totalOrders", totalOrders);
            
            // Calculate total revenue (sum of all paid orders)
            List<Order> paidOrders = orderRepository.findByPaymentStatus(Order.PaymentStatus.PAID);
            BigDecimal totalRevenue = paidOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.put("totalRevenue", totalRevenue);
            
            // Get pending orders count
            long pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING).size();
            stats.put("pendingOrders", pendingOrders);
            
            // Debug output
            System.out.println("Dashboard Stats: " + stats);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error calculating dashboard stats: " + e.getMessage());
            e.printStackTrace();
            
            // Provide default values
            stats.put("totalUsers", 0L);
            stats.put("totalProducts", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalRevenue", BigDecimal.ZERO);
            stats.put("pendingOrders", 0L);
        }
        
        return stats;
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getTotalProducts() {
        return productRepository.count();
    }
    
    public long getTotalOrders() {
        return orderRepository.count();
    }
    
    public BigDecimal getTotalRevenue() {
        List<Order> paidOrders = orderRepository.findByPaymentStatus(Order.PaymentStatus.PAID);
        return paidOrders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public long getPendingOrdersCount() {
        return orderRepository.findByStatus(Order.OrderStatus.PENDING).size();
    }
}
