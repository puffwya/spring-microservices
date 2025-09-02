package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return Map.of("message", "User registered successfully");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        return userRepo.findByEmail(user.getEmail())
                .filter(u -> encoder.matches(user.getPassword(), u.getPassword()))
                .map(u -> Map.of("token", jwtUtil.generateToken(u.getEmail())))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return Map.of("email", email);
    }
}

