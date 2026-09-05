package com.oj.platform.dto.response;

import com.oj.platform.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned upon successful user authentication or registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long id;
    private String username;
    private String email;
    private Role role;
    private long expiresIn;
}
