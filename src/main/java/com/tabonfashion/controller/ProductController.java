package com.tabonfashion.controller;

import com.tabonfashion.entity.Product;
import com.tabonfashion.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String showProducts(Model model,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String color,
                          @RequestParam(required = false) String size,
                          @RequestParam(required = false) BigDecimal minPrice,
                          @RequestParam(required = false) BigDecimal maxPrice,
                          @RequestParam(required = false) Boolean ecoFriendly,
                          @RequestParam(required = false) String search,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int pageSize,
                          @RequestParam(defaultValue = "id") String sortBy,
                          @RequestParam(defaultValue = "asc") String sortDir) {
    
    // Create Pageable object
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, pageSize, sort);
    
    Page<Product> productPage;
    
    if (search != null && !search.trim().isEmpty()) {
        productPage = productService.searchProducts(search, pageable);
    } else {
        productPage = productService.getFilteredProducts(category, color, size, minPrice, maxPrice, ecoFriendly, pageable);
    }
    
    model.addAttribute("productPage", productPage);
    model.addAttribute("products", productPage.getContent());
    model.addAttribute("categories", productService.getAllCategories());
    model.addAttribute("colors", productService.getAllColors());
    model.addAttribute("sizes", productService.getAllSizes());
    model.addAttribute("selectedCategory", category);
    model.addAttribute("selectedColor", color);
    model.addAttribute("selectedSize", size);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("ecoFriendly", ecoFriendly);
    model.addAttribute("search", search);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortDir", sortDir);
    
    return "products/list";
}
    
    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "products/details";
        }
        return "redirect:/products";
    }
    
    @GetMapping("/category/{category}")
    public String showProductsByCategory(@PathVariable String category, Model model) {
        List<Product> products = productService.getProductsByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "products/category";
    }
    
    @GetMapping("/eco-friendly")
    public String showEcoFriendlyProducts(Model model) {
        List<Product> products = productService.getEcoFriendlyProducts();
        model.addAttribute("products", products);
        model.addAttribute("title", "Eco-Friendly Products");
        return "products/eco-friendly";
    }
}
