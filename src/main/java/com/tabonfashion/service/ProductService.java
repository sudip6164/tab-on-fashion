package com.tabonfashion.service;

import com.tabonfashion.entity.Product;
import com.tabonfashion.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getEcoFriendlyProducts() {
        return productRepository.findEcoFriendlyProducts(BigDecimal.valueOf(8.0));
    }
    
    public List<Product> getInStockProducts() {
        return productRepository.findInStockProducts();
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }
    
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }
    
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    public List<String> getAllColors() {
        return productRepository.findAllColors();
    }
    
    public List<String> getAllSizes() {
        return productRepository.findAllSizes();
    }
    
    public List<Product> getFilteredProducts(String category, String color, String size, 
                                           BigDecimal minPrice, BigDecimal maxPrice, 
                                           Boolean ecoFriendly) {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
            .filter(p -> category == null || category.equals(p.getCategory()))
            .filter(p -> color == null || color.equals(p.getColor()))
            .filter(p -> size == null || size.equals(p.getSize()))
            .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
            .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
            .filter(p -> ecoFriendly == null || !ecoFriendly || p.isEcoFriendly())
            .toList();
    }

    public Page<Product> getFilteredProducts(String category, String color, String size,
                                               BigDecimal minPrice, BigDecimal maxPrice,
                                               Boolean ecoFriendly, Pageable pageable) {
        return productRepository.findFilteredProducts(category, color, size, minPrice, maxPrice, ecoFriendly, pageable);
    }
}
