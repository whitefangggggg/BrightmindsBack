package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.User;
import com.brightminds.brightminds_backend.repository.UserRepository;
import com.brightminds.brightminds_backend.util.JwtUtil;
import com.brightminds.brightminds_backend.dto.RegisterRequestDto;
import com.brightminds.brightminds_backend.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${teacher.code}")
    private String teacherCode;

    // Register a new user
    public User register(RegisterRequestDto registerRequest) {
        if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
            throw new AuthException("First name is required");
        }
        if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
            throw new AuthException("Last name is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            throw new AuthException("Password must be at least 6 characters long");
        }
        if (registerRequest.getRole() == null || !(registerRequest.getRole().equalsIgnoreCase("TEACHER") || registerRequest.getRole().equalsIgnoreCase("STUDENT"))) {
            throw new AuthException("Role must be either 'TEACHER' or 'STUDENT'");
        }
        if (registerRequest.getRole().equalsIgnoreCase("TEACHER")) {
            if (registerRequest.getTeacherCode() == null || !registerRequest.getTeacherCode().equals(teacherCode)) {
                throw new AuthException("Invalid or missing teacher code");
            }
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AuthException("User with email " + registerRequest.getEmail() + " already exists");
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setRole(registerRequest.getRole());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        return userRepository.save(user);
    }

    // Login a user and return token
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found with email: " + email));
        if (!user.getPassword().equals(password)) {
            throw new AuthException("Invalid password");
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