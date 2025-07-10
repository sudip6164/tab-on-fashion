package com.tabonfashion.controller;

import com.tabonfashion.dto.LoginRequest;
import com.tabonfashion.dto.SignupRequest;
import com.tabonfashion.dto.UserResponse;
import com.tabonfashion.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute SignupRequest signupRequest,
                                BindingResult bindingResult,
                                Model model) {

        model.addAttribute("loginRequest", new LoginRequest());

        // Show signup form on validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "signup");
            return "auth/login";
        }

        try {
            UserResponse user = userService.signup(signupRequest);
            model.addAttribute("successMessage", "Account created successfully! Please login.");
            model.addAttribute("activeTab", "login");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("activeTab", "signup");
        }

        return "auth/login";
    }
    
    // Show login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("signupRequest", new SignupRequest());
        return "auth/login";
    }
    
    // Process login
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute LoginRequest loginRequest,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        model.addAttribute("signupRequest", new SignupRequest());
        
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        
        try {
            UserResponse user = userService.login(loginRequest);
            
            // Store user in session
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRole", user.getRole());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Welcome back, " + user.getUsername() + "!");
            
            // Redirect based on role
            if (user.getRole() == com.tabonfashion.entity.User.Role.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/login";
        }
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/";
    }
}
