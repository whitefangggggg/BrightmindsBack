package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.User;
import com.brightminds.brightminds_backend.service.AuthService;
import com.brightminds.brightminds_backend.dto.LoginRequestDto;
import com.brightminds.brightminds_backend.dto.RegisterRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto registerRequest) {
        logger.info("Registering user with email: {}", registerRequest.getEmail());
        try {
            User registeredUser = authService.register(registerRequest);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        logger.info("Logging in user with email: {}", loginRequest.getEmail());
        try {
            var loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            logger.error("Error logging in user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}