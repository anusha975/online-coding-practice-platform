package com.oj.platform.service;

import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.dto.response.AuthResponse;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Role;
import com.oj.platform.exception.BadRequestException;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.security.JwtTokenProvider;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testcoder")
                .email("testcoder@example.com")
                .password("encodedPassword123")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("register() - Should successfully register new user")
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testcoder")
                .email("testcoder@example.com")
                .password("Password123!")
                .build();

        when(userRepository.existsByUsername("testcoder")).thenReturn(false);
        when(userRepository.existsByEmail("testcoder@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateTokenFromUsername(anyString(), anyLong(), anyString())).thenReturn("mocked.jwt.token");
        when(jwtTokenProvider.getExpirationTimeMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testcoder");
        assertThat(response.getEmail()).isEqualTo("testcoder@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
    }

    @Test
    @DisplayName("register() - Should throw BadRequestException when username is taken")
    void testRegisterDuplicateUsername() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testcoder")
                .email("new@example.com")
                .password("Password123!")
                .build();

        when(userRepository.existsByUsername("testcoder")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Username 'testcoder' is already taken");
    }

    @Test
    @DisplayName("register() - Should throw BadRequestException when email is already registered")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newcoder")
                .email("testcoder@example.com")
                .password("Password123!")
                .build();

        when(userRepository.existsByUsername("newcoder")).thenReturn(false);
        when(userRepository.existsByEmail("testcoder@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email 'testcoder@example.com' is already registered");
    }

    @Test
    @DisplayName("login() - Should successfully authenticate with credentials and return JWT")
    void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("testcoder")
                .password("Password123!")
                .build();

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = UserPrincipal.create(user);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");
        when(jwtTokenProvider.getExpirationTimeMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testcoder");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login() - Should throw BadCredentialsException on invalid credentials")
    void testLoginInvalidCredentials() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("testcoder")
                .password("WrongPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");
    }
}
