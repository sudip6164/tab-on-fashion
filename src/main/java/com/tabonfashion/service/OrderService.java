package com.tabonfashion.service;

import com.tabonfashion.entity.*;
import com.tabonfashion.repository.OrderRepository;
import com.tabonfashion.repository.OrderItemRepository;
import com.tabonfashion.repository.CartItemRepository;
import com.tabonfashion.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Transactional
    public Order createOrderFromCart(User user, String shippingAddress) {
        // Get cart
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (!cartOpt.isPresent()) {
            throw new RuntimeException("Cart not found for user");
        }
        
        Cart cart = cartOpt.get();
        
        // Get cart items with fresh query
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }
        
        // Calculate total from cart items
        BigDecimal totalAmount = cartItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create order
        Order order = new Order(user, totalAmount, shippingAddress);
        order = orderRepository.save(order);
        
        // Create order items from cart items (using data, not entities)
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItemRepository.save(orderItem);
        }
        
        // Clear cart items
        cartItemRepository.deleteByCart(cart);
        
        // Update cart total
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
        
        return order;
    }
    
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByDateDesc(userId);
    }
    
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrdersByDateDesc();
    }
    
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setPaymentStatus(paymentStatus);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
}
