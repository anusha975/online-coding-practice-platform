package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.enums.Role;
import com.oj.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register user and return JWT token")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("sarah_coder")
                .email("sarah@example.com")
                .password("StrongPass123!")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.username", is("sarah_coder")))
                .andExpect(jsonPath("$.data.email", is("sarah@example.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should reject duplicate username")
    void testRegisterDuplicateUsername() throws Exception {
        RegisterRequest user1 = RegisterRequest.builder()
                .username("coder1")
                .email("coder1@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        RegisterRequest duplicate = RegisterRequest.builder()
                .username("coder1")
                .email("different@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username 'coder1' is already taken.")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should authenticate with valid credentials")
    void testLoginSuccess() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .username("mike")
                .email("mike@example.com")
                .password("mikePassword123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Login with username
        LoginRequest loginReq = LoginRequest.builder()
                .usernameOrEmail("mike")
                .password("mikePassword123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.username", is("mike")));

        // Login with email
        LoginRequest loginWithEmail = LoginRequest.builder()
                .usernameOrEmail("mike@example.com")
                .password("mikePassword123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginWithEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should fail with bad password")
    void testLoginBadCredentials() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .username("david")
                .email("david@example.com")
                .password("correctPassword")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        LoginRequest badLogin = LoginRequest.builder()
                .usernameOrEmail("david")
                .password("wrongPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected Endpoints - /api/users/me should reject unauthenticated requests")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected Endpoints - /api/users/me should return user details with valid JWT")
    void testProtectedEndpointWithToken() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .username("emma")
                .email("emma@example.com")
                .password("emmaSecret123")
                .role(Role.USER)
                .build();

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username", is("emma")))
                .andExpect(jsonPath("$.data.email", is("emma@example.com")));
    }

    @Test
    @DisplayName("Role Authorization - ADMIN endpoint should allow ADMIN and reject USER (403)")
    void testRoleBasedAuthorization() throws Exception {
        // 1. Register normal USER
        RegisterRequest userReq = RegisterRequest.builder()
                .username("regular_user")
                .email("regular@example.com")
                .password("regularPass123")
                .role(Role.USER)
                .build();

        String userResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andReturn().getResponse().getContentAsString();
        String userToken = objectMapper.readTree(userResponse).path("data").path("token").asText();

        // 2. Register ADMIN
        RegisterRequest adminReq = RegisterRequest.builder()
                .username("admin_user")
                .email("admin@example.com")
                .password("adminPass123")
                .role(Role.ADMIN)
                .build();

        String adminResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminReq)))
                .andReturn().getResponse().getContentAsString();
        String adminToken = objectMapper.readTree(adminResponse).path("data").path("token").asText();

        // 3. Normal user accessing admin endpoint -> 403 Forbidden
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode", is(403)))
                .andExpect(jsonPath("$.message", is("You do not have permission to access this resource.")));

        // 4. Admin accessing admin endpoint -> 200 OK
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message", notNullValue()));
    }
}
