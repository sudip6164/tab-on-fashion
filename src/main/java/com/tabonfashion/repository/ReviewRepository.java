package com.tabonfashion.repository;

import com.tabonfashion.entity.Product;
import com.tabonfashion.entity.Review;
import com.tabonfashion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProduct(Product product);
    
    List<Review> findByUser(User user);
    
    List<Review> findByProductId(Long productId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countReviewsByProductId(@Param("productId") Long productId);
    
    List<Review> findByVerifiedPurchaseTrue();
}
