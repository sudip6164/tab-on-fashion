package com.tabonfashion.service;

import com.tabonfashion.dto.LoginRequest;
import com.tabonfashion.dto.SignupRequest;
import com.tabonfashion.dto.UserResponse;
import com.tabonfashion.entity.User;
import com.tabonfashion.repository.UserRepository;
import com.tabonfashion.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserResponse signup(SignupRequest signupRequest) throws Exception {
        // Validate password match
        if (!signupRequest.isPasswordMatching()) {
            throw new Exception("Passwords do not match");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new Exception("Username is already taken");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new Exception("Email is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(PasswordUtil.hashPassword(signupRequest.getPassword()));
        user.setRole(User.Role.USER);
        
        // Save user
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }
    
    public UserResponse login(LoginRequest loginRequest) throws Exception {
        // Find user by username or email
        Optional<User> userOptional = userRepository.findByEmail(
            loginRequest.getEmail()
        );
        
        if (userOptional.isEmpty()) {
            throw new Exception("Invalid username/email or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!PasswordUtil.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Invalid username/email or password");
        }
        
        return new UserResponse(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Admin methods
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public void updateUser(Long id, String username, String email, User.Role role) throws Exception {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if username is taken by another user
            if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
                throw new Exception("Username is already taken");
            }
            
            // Check if email is taken by another user
            if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                throw new Exception("Email is already registered");
            }
            
            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new Exception("User not found");
        }
    }
    
    public void deleteUser(Long id) throws Exception {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new Exception("User not found");
        }
    }
}
