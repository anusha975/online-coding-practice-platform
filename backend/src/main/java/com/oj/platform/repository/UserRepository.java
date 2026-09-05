package com.oj.platform.repository;

import com.oj.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user already exists with the given username.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user already exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a username is taken by any other user except the current ID (for updates).
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Checks if an email is taken by any other user except the current ID (for updates).
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(String email);
}
