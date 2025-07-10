package com.tabonfashion.repository;

import com.tabonfashion.entity.Order;
import com.tabonfashion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersByDateDesc();
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserIdOrderByDateDesc(Long userId);
}
