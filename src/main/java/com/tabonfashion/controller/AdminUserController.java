package com.tabonfashion.controller;

import com.tabonfashion.dto.SignupRequest;
import com.tabonfashion.dto.UserResponse;
import com.tabonfashion.entity.User;
import com.tabonfashion.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listUsers(Model model, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           HttpSession session) {
        
        // Check admin access
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userService.getAllUsers(pageable);
        
        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        
        return "admin/users/list";
    }
    
    @GetMapping("/create")
    public String showCreateUserForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("signupRequest", new SignupRequest());
        return "admin/users/create";
    }
    
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute SignupRequest signupRequest,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        if (bindingResult.hasErrors()) {
            return "admin/users/create";
        }
        
        try {
            UserResponse user = userService.signup(signupRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User created successfully: " + user.getUsername());
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/users/create";
        }
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "admin/users/view";
        }
        
        return "redirect:/admin/users";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "admin/users/edit";
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                            @RequestParam String username,
                            @RequestParam String email,
                            @RequestParam User.Role role,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            userService.updateUser(id, username, email, role);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/users/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, 
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    private boolean isAdmin(HttpSession session) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null && userRole.toString().equals("ADMIN");
    }
}
