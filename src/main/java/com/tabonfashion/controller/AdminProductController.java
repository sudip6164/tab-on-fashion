package com.tabonfashion.controller;

import com.tabonfashion.entity.Product;
import com.tabonfashion.service.ProductService;
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

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String listProducts(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              HttpSession session) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productService.getAllProducts(pageable);
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        return "admin/products/products";
    }
    
    @GetMapping("/create")
    public String showCreateProductForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("product", new Product());
        return "admin/products/create";
    }
    
    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Product created successfully: " + product.getName());
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error creating product: " + e.getMessage());
            return "redirect:/admin/products/create";
        }
    }
    
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "admin/products/view";
        }
        
        return "redirect:/admin/products";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "admin/products/edit";
        }
        
        return "redirect:/admin/products";
    }
    
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                               @ModelAttribute Product product,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            product.setId(id);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/admin/products/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
    
    private boolean isAdmin(HttpSession session) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null && userRole.toString().equals("ADMIN");
    }
}
