package com.oj.platform.service.impl;

import com.oj.platform.dto.request.UserCreateRequest;
import com.oj.platform.dto.request.UserUpdateRequest;
import com.oj.platform.dto.response.UserResponse;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Role;
import com.oj.platform.exception.BadRequestException;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Production implementation of UserService with password encoding.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Attempting to create user with username: {} and email: {}", request.getUsername(), request.getEmail());

        // 1. Business Validation: Unique Username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("Username '%s' is already taken.", request.getUsername()));
        }

        // 2. Business Validation: Unique Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(String.format("Email '%s' is already registered.", request.getEmail()));
        }

        // 3. Entity construction with BCrypt password hashing
        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        // 4. Persistence
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        // 5. Return DTO (never entity directly)
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users.");

        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Attempting to update user ID: {}", id);

        // 1. Fetch existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 2. Business Validation: Check if new username is taken by someone else
        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new BadRequestException(String.format("Username '%s' is already taken by another user.", request.getUsername()));
        }

        // 3. Business Validation: Check if new email is taken by someone else
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BadRequestException(String.format("Email '%s' is already registered by another user.", request.getEmail()));
        }

        // 4. Update entity fields
        existingUser.setUsername(request.getUsername().trim());
        existingUser.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }

        // 5. Save and return DTO
        User updatedUser = userRepository.save(existingUser);
        log.info("User ID: {} updated successfully.", updatedUser.getId());

        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Attempting to delete user ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
        log.info("User ID: {} deleted successfully.", id);
    }
}
