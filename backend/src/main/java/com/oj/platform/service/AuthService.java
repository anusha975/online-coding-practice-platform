package com.oj.platform.service;

import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.dto.response.AuthResponse;

/**
 * Service interface for user registration and authentication.
 */
public interface AuthService {

    /**
     * Registers a new user, hashes password, saves to DB, and returns JWT auth response.
     *
     * @param request registration details
     * @return AuthResponse containing token and user info
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates user credentials and generates a signed JWT token.
     *
     * @param request login payload (username/email and password)
     * @return AuthResponse containing token and user info
     */
    AuthResponse login(LoginRequest request);
}
