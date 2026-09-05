package com.oj.platform.service.impl;

import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.dto.response.AuthResponse;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Role;
import com.oj.platform.exception.BadRequestException;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.security.JwtTokenProvider;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService for user registration and JWT authentication.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for username: {}", request.getUsername());

        // 1. Check if username is already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("Username '%s' is already taken.", request.getUsername()));
        }

        // 2. Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(String.format("Email '%s' is already registered.", request.getEmail()));
        }

        // 3. Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Create and persist User entity
        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(hashedPassword)
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // 5. Generate JWT token
        String token = tokenProvider.generateTokenFromUsername(
                savedUser.getUsername(),
                savedUser.getId(),
                savedUser.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .expiresIn(tokenProvider.getExpirationTimeMs())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login attempt for: {}", request.getUsernameOrEmail());

        // 1. Authenticate user credentials using Spring Security AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail().trim(),
                        request.getPassword()
                )
        );

        // 2. Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("User logged in successfully: {}", userPrincipal.getUsername());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .role(userPrincipal.getRole())
                .expiresIn(tokenProvider.getExpirationTimeMs())
                .build();
    }
}
