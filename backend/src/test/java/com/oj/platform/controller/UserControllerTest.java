package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.UserCreateRequest;
import com.oj.platform.dto.request.UserUpdateRequest;
import com.oj.platform.enums.Role;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users - Should successfully create a user and return 201")
    void testCreateUserSuccess() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("securePassword123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.username", is("john_doe")))
                .andExpect(jsonPath("$.data.email", is("john@example.com")))
                .andExpect(jsonPath("$.data.role", is("USER")))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 on duplicate username")
    void testCreateUserDuplicateUsername() throws Exception {
        UserCreateRequest user1 = UserCreateRequest.builder()
                .username("alex")
                .email("alex1@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        UserCreateRequest duplicateUsername = UserCreateRequest.builder()
                .username("alex")
                .email("alex2@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username 'alex' is already taken.")));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 on duplicate email")
    void testCreateUserDuplicateEmail() throws Exception {
        UserCreateRequest user1 = UserCreateRequest.builder()
                .username("alex1")
                .email("alex@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        UserCreateRequest duplicateEmail = UserCreateRequest.builder()
                .username("alex2")
                .email("alex@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email 'alex@example.com' is already registered.")));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 on validation failure (invalid email format)")
    void testCreateUserInvalidEmail() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("validUser")
                .email("not-an-email")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.email", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user by ID")
    void testGetUserByIdSuccess() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("alice")
                .email("alice@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(get("/api/users/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(createdId.intValue())))
                .andExpect(jsonPath("$.data.username", is("alice")))
                .andExpect(jsonPath("$.data.role", is("ADMIN")));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 for non-existent user")
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("User not found with id : '99999'")));
    }

    @Test
    @DisplayName("GET /api/users - Should return list of all users")
    void testGetAllUsers() throws Exception {
        UserCreateRequest user1 = UserCreateRequest.builder()
                .username("user1")
                .email("user1@example.com")
                .password("pass1234")
                .build();

        UserCreateRequest user2 = UserCreateRequest.builder()
                .username("user2")
                .email("user2@example.com")
                .password("pass1234")
                .build();

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user1)));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user2)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].username", is("user1")))
                .andExpect(jsonPath("$.data[1].username", is("user2")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user details")
    void testUpdateUserSuccess() throws Exception {
        UserCreateRequest createReq = UserCreateRequest.builder()
                .username("charlie")
                .email("charlie@example.com")
                .password("pass1234")
                .role(Role.USER)
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(response).path("data").path("id").asLong();

        UserUpdateRequest updateReq = UserUpdateRequest.builder()
                .username("charlie_updated")
                .email("charlie_new@example.com")
                .role(Role.ADMIN)
                .build();

        mockMvc.perform(put("/api/users/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username", is("charlie_updated")))
                .andExpect(jsonPath("$.data.email", is("charlie_new@example.com")))
                .andExpect(jsonPath("$.data.role", is("ADMIN")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user by ID")
    void testDeleteUserSuccess() throws Exception {
        UserCreateRequest createReq = UserCreateRequest.builder()
                .username("todelete")
                .email("todelete@example.com")
                .password("pass1234")
                .build();

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(delete("/api/users/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User deleted successfully")));

        mockMvc.perform(get("/api/users/" + createdId))
                .andExpect(status().isNotFound());
    }
}
