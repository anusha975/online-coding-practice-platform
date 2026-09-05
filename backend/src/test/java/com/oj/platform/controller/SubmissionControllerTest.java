package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.dto.request.SubmissionCreateRequest;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.Role;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String user1Token;
    private String user2Token;
    private String adminToken;
    private Long problemId;

    @BeforeEach
    void setUp() throws Exception {
        submissionRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        // Register User 1
        RegisterRequest user1Req = RegisterRequest.builder()
                .username("user_one")
                .email("user1@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        String u1Res = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user1Req)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        user1Token = objectMapper.readTree(u1Res).path("data").path("token").asText();

        // Register User 2
        RegisterRequest user2Req = RegisterRequest.builder()
                .username("user_two")
                .email("user2@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        String u2Res = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user2Req)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        user2Token = objectMapper.readTree(u2Res).path("data").path("token").asText();

        // Register Admin
        RegisterRequest adminReq = RegisterRequest.builder()
                .username("admin_boss")
                .email("admin@example.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();
        String adminRes = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(adminReq)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminRes).path("data").path("token").asText();

        // Create a Problem as Admin
        ProblemCreateRequest problemReq = ProblemCreateRequest.builder()
                .title("Two Sum")
                .description("Find indices")
                .difficulty(Difficulty.EASY)
                .category("Arrays")
                .build();
        String pRes = mockMvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problemReq)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        problemId = objectMapper.readTree(pRes).path("data").path("id").asLong();
    }

    @Test
    @DisplayName("POST /api/submissions - User should submit Java code and receive PENDING status")
    void testSubmitJavaCodeSuccess() throws Exception {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(problemId)
                .language(Language.JAVA)
                .sourceCode("public class Solution { public static void main(String[] args) {} }")
                .build();

        mockMvc.perform(post("/api/submissions")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.language", is("JAVA")))
                .andExpect(jsonPath("$.data.username", is("user_one")))
                .andExpect(jsonPath("$.data.problemTitle", is("Two Sum")));
    }

    @Test
    @DisplayName("POST /api/submissions - User should submit Python code")
    void testSubmitPythonCodeSuccess() throws Exception {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(problemId)
                .language(Language.PYTHON)
                .sourceCode("print('Hello World')")
                .build();

        mockMvc.perform(post("/api/submissions")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.language", is("PYTHON")));
    }

    @Test
    @DisplayName("POST /api/submissions - Should reject unauthenticated submission (401)")
    void testSubmitUnauthenticated() throws Exception {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(problemId)
                .language(Language.JAVA)
                .sourceCode("class Solution {}")
                .build();

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/submissions - Should reject invalid problem ID (404)")
    void testSubmitInvalidProblem() throws Exception {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(99999L)
                .language(Language.JAVA)
                .sourceCode("class Solution {}")
                .build();

        mockMvc.perform(post("/api/submissions")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Problem not found with id : '99999'")));
    }

    @Test
    @DisplayName("GET /api/submissions/{id} - Owner and Admin can view submission, but other users are forbidden (403)")
    void testSubmissionAccessControl() throws Exception {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(problemId)
                .language(Language.JAVA)
                .sourceCode("User 1 Secret Code")
                .build();

        String res = mockMvc.perform(post("/api/submissions")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Long submissionId = objectMapper.readTree(res).path("data").path("id").asLong();

        // 1. Owner (User 1) accesses -> 200 OK
        mockMvc.perform(get("/api/submissions/" + submissionId)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sourceCode", is("User 1 Secret Code")));

        // 2. Other User (User 2) accesses -> 403 Forbidden
        mockMvc.perform(get("/api/submissions/" + submissionId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You do not have permission to view this submission.")));

        // 3. Admin accesses -> 200 OK
        mockMvc.perform(get("/api/submissions/" + submissionId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sourceCode", is("User 1 Secret Code")));
    }

    @Test
    @DisplayName("GET /api/submissions/my - User should get only their own submission history")
    void testGetMySubmissions() throws Exception {
        // User 1 submits 2 solutions
        SubmissionCreateRequest s1 = SubmissionCreateRequest.builder().problemId(problemId).language(Language.JAVA).sourceCode("code 1").build();
        SubmissionCreateRequest s2 = SubmissionCreateRequest.builder().problemId(problemId).language(Language.PYTHON).sourceCode("code 2").build();
        mockMvc.perform(post("/api/submissions").header("Authorization", "Bearer " + user1Token).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s1)));
        mockMvc.perform(post("/api/submissions").header("Authorization", "Bearer " + user1Token).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s2)));

        // User 2 submits 1 solution
        SubmissionCreateRequest s3 = SubmissionCreateRequest.builder().problemId(problemId).language(Language.JAVA).sourceCode("user 2 code").build();
        mockMvc.perform(post("/api/submissions").header("Authorization", "Bearer " + user2Token).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s3)));

        // User 1 queries /my -> should return exactly 2 items
        mockMvc.perform(get("/api/submissions/my")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", is(2)));

        // User 2 queries /my -> should return exactly 1 item
        mockMvc.perform(get("/api/submissions/my")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements", is(1)));
    }

    @Test
    @DisplayName("GET /api/submissions/problem/{problemId} - Returns submissions for target problem by current user")
    void testGetSubmissionsByProblem() throws Exception {
        SubmissionCreateRequest s1 = SubmissionCreateRequest.builder().problemId(problemId).language(Language.JAVA).sourceCode("code 1").build();
        mockMvc.perform(post("/api/submissions").header("Authorization", "Bearer " + user1Token).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(s1)));

        mockMvc.perform(get("/api/submissions/problem/" + problemId)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].problemId", is(problemId.intValue())));
    }
}
