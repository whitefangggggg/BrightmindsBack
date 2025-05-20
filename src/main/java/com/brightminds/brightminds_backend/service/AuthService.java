package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.User;
import com.brightminds.brightminds_backend.repository.UserRepository;
import com.brightminds.brightminds_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user
    public User register(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        if (user.getRole() == null || !(user.getRole().equalsIgnoreCase("TEACHER") || user.getRole().equalsIgnoreCase("STUDENT"))) {
            throw new RuntimeException("Role must be either 'TEACHER' or 'STUDENT'");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    // Login a user and return token
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user);
    }

    public static class LoginResponse {
        private String accessToken;
        private User user;

        public LoginResponse(String accessToken, User user) {
            this.accessToken = accessToken;
            this.user = user;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public User getUser() {
            return user;
        }
    }
}