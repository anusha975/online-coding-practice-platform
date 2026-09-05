package com.oj.platform.service;

import com.oj.platform.dto.request.UserCreateRequest;
import com.oj.platform.dto.request.UserUpdateRequest;
import com.oj.platform.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface defining user management operations.
 */
public interface UserService {

    /**
     * Registers / creates a new user.
     *
     * @param request user creation payload.
     * @return UserResponse containing created user details.
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Retrieves user by unique ID.
     *
     * @param id User ID.
     * @return UserResponse details.
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves all registered users.
     *
     * @return List of UserResponse objects.
     */
    List<UserResponse> getAllUsers();

    /**
     * Updates an existing user's information.
     *
     * @param id User ID to update.
     * @param request update payload.
     * @return Updated UserResponse details.
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Deletes a user by ID.
     *
     * @param id User ID to delete.
     */
    void deleteUser(Long id);
}
