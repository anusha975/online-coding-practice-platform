package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.ProblemUpdateRequest;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
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
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/problems - Should be publicly accessible without authentication")
    void testGetProblemsPublicAccess() throws Exception {
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/problems - Should allow ADMIN to create a problem")
    @WithMockUser(roles = "ADMIN")
    void testCreateProblemAsAdmin() throws Exception {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Two Sum")
                .description("Given an array of integers nums and an integer target, return indices...")
                .difficulty(Difficulty.EASY)
                .category("Arrays")
                .constraints("2 <= nums.length <= 104")
                .inputFormat("nums = [2,7,11,15], target = 9")
                .outputFormat("[0,1]")
                .sampleInput("[2,7,11,15]\n9")
                .sampleOutput("[0,1]")
                .timeLimitMs(2000)
                .memoryLimitMb(256)
                .build();

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.title", is("Two Sum")))
                .andExpect(jsonPath("$.data.difficulty", is("EASY")))
                .andExpect(jsonPath("$.data.category", is("Arrays")));
    }

    @Test
    @DisplayName("POST /api/problems - Should reject regular USER (403 Forbidden)")
    @WithMockUser(roles = "USER")
    void testCreateProblemAsUserForbidden() throws Exception {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Reverse Linked List")
                .description("Reverse a singly linked list.")
                .difficulty(Difficulty.EASY)
                .category("Linked Lists")
                .build();

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/problems - Should reject unauthenticated request (401 Unauthorized)")
    void testCreateProblemUnauthenticated() throws Exception {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Valid Palindrome")
                .description("Check if string is palindrome.")
                .difficulty(Difficulty.EASY)
                .category("Strings")
                .build();

        mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/problems/{id} - Should return problem details publicly")
    @WithMockUser(roles = "ADMIN")
    void testGetProblemByIdPublic() throws Exception {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Binary Tree Inorder Traversal")
                .description("Given root of a binary tree, return inorder traversal.")
                .difficulty(Difficulty.MEDIUM)
                .category("Trees")
                .build();

        String response = mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        // Query without any auth
        mockMvc.perform(get("/api/problems/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Binary Tree Inorder Traversal")))
                .andExpect(jsonPath("$.data.category", is("Trees")));
    }

    @Test
    @DisplayName("GET /api/problems - Should filter by Difficulty and Category")
    @WithMockUser(roles = "ADMIN")
    void testFilterAndSearchProblems() throws Exception {
        // Create Problem 1: Easy Array
        ProblemCreateRequest p1 = ProblemCreateRequest.builder()
                .title("Contains Duplicate")
                .description("Find if array contains any duplicates.")
                .difficulty(Difficulty.EASY)
                .category("Arrays")
                .build();
        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)));

        // Create Problem 2: Hard Dynamic Programming
        ProblemCreateRequest p2 = ProblemCreateRequest.builder()
                .title("Trapping Rain Water")
                .description("Compute how much water it can trap after raining.")
                .difficulty(Difficulty.HARD)
                .category("Dynamic Programming")
                .build();
        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p2)));

        // Create Problem 3: Medium Trees
        ProblemCreateRequest p3 = ProblemCreateRequest.builder()
                .title("Validate Binary Search Tree")
                .description("Determine if binary tree is valid BST.")
                .difficulty(Difficulty.MEDIUM)
                .category("Trees")
                .build();
        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p3)));

        // 1. Filter by difficulty: HARD
        mockMvc.perform(get("/api/problems?difficulty=HARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Trapping Rain Water")));

        // 2. Filter by category: Trees
        mockMvc.perform(get("/api/problems?category=Trees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Validate Binary Search Tree")));

        // 3. Search keyword: "duplicate"
        mockMvc.perform(get("/api/problems?search=duplicate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Contains Duplicate")));

        // 4. Pagination: page=0, size=2
        mockMvc.perform(get("/api/problems?page=0&size=2&sortBy=title&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", is(3)))
                .andExpect(jsonPath("$.data.totalPages", is(2)));

        // 5. Multiple Filters combined: Search + Difficulty + Category
        mockMvc.perform(get("/api/problems?search=rain&difficulty=HARD&category=Dynamic Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Trapping Rain Water")));
    }

    @Test
    @DisplayName("GET /api/problems/categories - Should return list of distinct sorted categories")
    @WithMockUser(roles = "ADMIN")
    void testGetDistinctCategories() throws Exception {
        ProblemCreateRequest p1 = ProblemCreateRequest.builder()
                .title("Problem A")
                .description("Desc A")
                .difficulty(Difficulty.EASY)
                .category("Strings")
                .build();
        ProblemCreateRequest p2 = ProblemCreateRequest.builder()
                .title("Problem B")
                .description("Desc B")
                .difficulty(Difficulty.MEDIUM)
                .category("Arrays")
                .build();
        ProblemCreateRequest p3 = ProblemCreateRequest.builder()
                .title("Problem C")
                .description("Desc C")
                .difficulty(Difficulty.HARD)
                .category("Strings")
                .build();

        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)));
        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p2)));
        mockMvc.perform(post("/api/problems").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p3)));

        mockMvc.perform(get("/api/problems/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0]", is("Arrays")))
                .andExpect(jsonPath("$.data[1]", is("Strings")));
    }

    @Test
    @DisplayName("PUT /api/problems/{id} - Should allow ADMIN to update problem")
    @WithMockUser(roles = "ADMIN")
    void testUpdateProblemAsAdmin() throws Exception {
        ProblemCreateRequest createReq = ProblemCreateRequest.builder()
                .title("Climbing Stairs")
                .description("Original description")
                .difficulty(Difficulty.EASY)
                .category("Dynamic Programming")
                .build();

        String response = mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        ProblemUpdateRequest updateReq = ProblemUpdateRequest.builder()
                .title("Climbing Stairs (Updated)")
                .description("You are climbing a staircase. It takes n steps...")
                .difficulty(Difficulty.EASY)
                .category("Dynamic Programming")
                .timeLimitMs(1500)
                .memoryLimitMb(128)
                .build();

        mockMvc.perform(put("/api/problems/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Climbing Stairs (Updated)")))
                .andExpect(jsonPath("$.data.timeLimitMs", is(1500)))
                .andExpect(jsonPath("$.data.memoryLimitMb", is(128)));
    }

    @Test
    @DisplayName("DELETE /api/problems/{id} - Should allow ADMIN to delete problem")
    @WithMockUser(roles = "ADMIN")
    void testDeleteProblemAsAdmin() throws Exception {
        ProblemCreateRequest createReq = ProblemCreateRequest.builder()
                .title("Problem to Delete")
                .description("Temp problem description")
                .difficulty(Difficulty.EASY)
                .category("Arrays")
                .build();

        String response = mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(delete("/api/problems/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Problem deleted successfully")));

        mockMvc.perform(get("/api/problems/" + id))
                .andExpect(status().isNotFound());
    }
}
