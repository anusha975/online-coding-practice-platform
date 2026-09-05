package com.oj.platform.controller;

import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.AuthResponse;
import com.oj.platform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User Authentication (Registration & Login).
 *
 * Base Path: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user and issues a JWT token.
     *
     * @param request validated registration details.
     * @return 201 Created with AuthResponse.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully", response),
                HttpStatus.CREATED
        );
    }

    /**
     * Authenticates user credentials and issues a JWT token.
     *
     * @param request validated login credentials.
     * @return 200 OK with AuthResponse.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
