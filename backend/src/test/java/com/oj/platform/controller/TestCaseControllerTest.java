package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.TestCaseCreateRequest;
import com.oj.platform.dto.request.TestCaseUpdateRequest;
import com.oj.platform.enums.Difficulty;
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
@TestPropertySource(locations = "classpath:application-test.properties")
class TestCaseControllerTest {

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

    private Long testProblemId;

    @BeforeEach
    void setUp() throws Exception {
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test problem
        ProblemCreateRequest problemReq = ProblemCreateRequest.builder()
                .title("Two Sum Problem")
                .description("Two sum description")
                .difficulty(Difficulty.EASY)
                .category("Arrays")
                .build();

        String res = mockMvc.perform(post("/api/problems")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problemReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        testProblemId = objectMapper.readTree(res).path("data").path("id").asLong();
    }

    @Test
    @DisplayName("POST /api/admin/problems/{id}/testcases - Should allow ADMIN to add public and hidden test cases")
    @WithMockUser(roles = "ADMIN")
    void testCreateTestCaseAsAdmin() throws Exception {
        TestCaseCreateRequest sampleReq = TestCaseCreateRequest.builder()
                .input("2 7 11 15\n9")
                .expectedOutput("0 1")
                .hidden(false)
                .build();

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.hidden", is(false)))
                .andExpect(jsonPath("$.data.input", is("2 7 11 15\n9")));

        TestCaseCreateRequest hiddenReq = TestCaseCreateRequest.builder()
                .input("3 2 4\n6")
                .expectedOutput("1 2")
                .hidden(true)
                .build();

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hiddenReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.hidden", is(true)));
    }

    @Test
    @DisplayName("POST /api/admin/problems/{id}/testcases - Should reject regular USER (403 Forbidden)")
    @WithMockUser(roles = "USER")
    void testCreateTestCaseAsUserForbidden() throws Exception {
        TestCaseCreateRequest req = TestCaseCreateRequest.builder()
                .input("1 2 3")
                .expectedOutput("6")
                .hidden(false)
                .build();

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/problems/{id}/testcases/sample - Must ONLY return public sample cases and NEVER leak hidden cases")
    void testGetSampleTestCasesNeverLeaksHidden() throws Exception {
        TestCaseCreateRequest publicCase = TestCaseCreateRequest.builder()
                .input("Public Input")
                .expectedOutput("Public Output")
                .hidden(false)
                .build();

        TestCaseCreateRequest hiddenCase1 = TestCaseCreateRequest.builder()
                .input("Secret Input 1")
                .expectedOutput("Secret Output 1")
                .hidden(true)
                .build();

        TestCaseCreateRequest hiddenCase2 = TestCaseCreateRequest.builder()
                .input("Secret Input 2")
                .expectedOutput("Secret Output 2")
                .hidden(true)
                .build();

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publicCase)));

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hiddenCase1)));

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hiddenCase2)));

        mockMvc.perform(get("/api/problems/" + testProblemId + "/testcases/sample"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].input", is("Public Input")))
                .andExpect(jsonPath("$.data[0].expectedOutput", is("Public Output")))
                .andExpect(jsonPath("$.data[0].hidden").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/admin/problems/{id}/testcases - Admin should see all test cases including hidden")
    @WithMockUser(roles = "ADMIN")
    void testAdminGetsAllTestCases() throws Exception {
        TestCaseCreateRequest publicCase = TestCaseCreateRequest.builder()
                .input("Input 1")
                .expectedOutput("Output 1")
                .hidden(false)
                .build();

        TestCaseCreateRequest hiddenCase = TestCaseCreateRequest.builder()
                .input("Input 2")
                .expectedOutput("Output 2")
                .hidden(true)
                .build();

        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(publicCase)));
        mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(hiddenCase)));

        mockMvc.perform(get("/api/admin/problems/" + testProblemId + "/testcases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].hidden", is(false)))
                .andExpect(jsonPath("$.data[1].hidden", is(true)));
    }

    @Test
    @DisplayName("PUT /api/admin/testcases/{id} - Should update a test case")
    @WithMockUser(roles = "ADMIN")
    void testUpdateTestCase() throws Exception {
        TestCaseCreateRequest createReq = TestCaseCreateRequest.builder()
                .input("Old Input")
                .expectedOutput("Old Output")
                .hidden(true)
                .build();

        String res = mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long testCaseId = objectMapper.readTree(res).path("data").path("id").asLong();

        TestCaseUpdateRequest updateReq = TestCaseUpdateRequest.builder()
                .input("New Input")
                .expectedOutput("New Output")
                .hidden(false)
                .build();

        mockMvc.perform(put("/api/admin/testcases/" + testCaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.input", is("New Input")))
                .andExpect(jsonPath("$.data.expectedOutput", is("New Output")))
                .andExpect(jsonPath("$.data.hidden", is(false)));
    }

    @Test
    @DisplayName("DELETE /api/admin/testcases/{id} - Should delete a test case")
    @WithMockUser(roles = "ADMIN")
    void testDeleteTestCase() throws Exception {
        TestCaseCreateRequest createReq = TestCaseCreateRequest.builder()
                .input("Delete Input")
                .expectedOutput("Delete Output")
                .hidden(true)
                .build();

        String res = mockMvc.perform(post("/api/admin/problems/" + testProblemId + "/testcases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long testCaseId = objectMapper.readTree(res).path("data").path("id").asLong();

        mockMvc.perform(delete("/api/admin/testcases/" + testCaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Test case deleted successfully")));

        mockMvc.perform(get("/api/admin/testcases/" + testCaseId))
                .andExpect(status().isNotFound());
    }
}
