package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.LoginRequest;
import com.oj.platform.dto.request.RegisterRequest;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Language;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.Submission;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.entity.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    private String userToken;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Register and Login User
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("stat_coder")
                .email("stat_coder@example.com")
                .password("Password123!")
                .build();
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("stat_coder")
                .password("Password123!")
                .build();
        String loginRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        userToken = objectMapper.readTree(loginRes).path("data").path("token").asText();

        User user = userRepository.findByUsername("stat_coder").orElseThrow();
        userId = user.getId();

        // 2. Setup Problems: 1 Easy, 1 Medium, 1 Hard
        Problem pEasy = problemRepository.save(Problem.builder()
                .title("Easy Math")
                .description("Desc")
                .difficulty(com.oj.platform.enums.Difficulty.EASY)
                .category("Math")
                .build());

        Problem pMedium = problemRepository.save(Problem.builder()
                .title("Medium DP")
                .description("Desc")
                .difficulty(com.oj.platform.enums.Difficulty.MEDIUM)
                .category("DP")
                .build());

        Problem pHard = problemRepository.save(Problem.builder()
                .title("Hard Graph")
                .description("Desc")
                .difficulty(com.oj.platform.enums.Difficulty.HARD)
                .category("Graphs")
                .build());

        // 3. Seed Submissions:
        // Problem Easy: 1 WA, then 1 AC
        submissionRepository.save(Submission.builder()
                .user(user).problem(pEasy).sourceCode("wa code").language(com.oj.platform.enums.Language.JAVA)
                .status(com.oj.platform.enums.SubmissionStatus.WRONG_ANSWER).build());
        submissionRepository.save(Submission.builder()
                .user(user).problem(pEasy).sourceCode("ac code").language(com.oj.platform.enums.Language.JAVA)
                .status(com.oj.platform.enums.SubmissionStatus.ACCEPTED).build());

        // Problem Medium: 1 AC
        submissionRepository.save(Submission.builder()
                .user(user).problem(pMedium).sourceCode("ac py code").language(com.oj.platform.enums.Language.PYTHON)
                .status(com.oj.platform.enums.SubmissionStatus.ACCEPTED).build());

        // Problem Hard: 1 WA
        submissionRepository.save(Submission.builder()
                .user(user).problem(pHard).sourceCode("hard wa code").language(com.oj.platform.enums.Language.JAVA)
                .status(com.oj.platform.enums.SubmissionStatus.WRONG_ANSWER).build());
    }

    @Test
    @DisplayName("GET /api/users/me/stats - Should accurately calculate submissions, distinct solved, and difficulty breakdown")
    void testGetMyStats() throws Exception {
        mockMvc.perform(get("/api/users/me/stats")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("stat_coder")))
                .andExpect(jsonPath("$.data.totalSubmissions", is(4)))
                .andExpect(jsonPath("$.data.acceptedSubmissions", is(2)))
                .andExpect(jsonPath("$.data.acceptanceRate", is(50.0)))
                .andExpect(jsonPath("$.data.totalSolved", is(2))) // Distinct: pEasy + pMedium
                .andExpect(jsonPath("$.data.easySolved", is(1)))
                .andExpect(jsonPath("$.data.mediumSolved", is(1)))
                .andExpect(jsonPath("$.data.hardSolved", is(0)))
                .andExpect(jsonPath("$.data.totalProblems", is(3)))
                .andExpect(jsonPath("$.data.totalEasyProblems", is(1)))
                .andExpect(jsonPath("$.data.totalMediumProblems", is(1)))
                .andExpect(jsonPath("$.data.totalHardProblems", is(1)));
    }

    @Test
    @DisplayName("GET /api/users/{id}/stats - Public endpoint should return accurate stats for target user")
    void testGetUserStatsById() throws Exception {
        mockMvc.perform(get("/api/users/" + userId + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.data.totalSolved", is(2)))
                .andExpect(jsonPath("$.data.acceptanceRate", is(50.0)));
    }

    @Test
    @DisplayName("GET /api/users/{id}/stats - Should return 404 if user does not exist")
    void testGetUserStatsNotFound() throws Exception {
        mockMvc.perform(get("/api/users/99999/stats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)));
    }
}
