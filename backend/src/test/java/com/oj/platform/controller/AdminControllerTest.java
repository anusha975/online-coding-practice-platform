package com.oj.platform.controller;

import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Test
    @DisplayName("GET /api/admin/stats - Should allow ADMIN to view dashboard stats")
    @WithMockUser(roles = "ADMIN")
    void testGetAdminStatsAsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalUsers", notNullValue()))
                .andExpect(jsonPath("$.data.totalProblems", notNullValue()))
                .andExpect(jsonPath("$.data.totalSubmissions", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/admin/stats - Should reject regular USER (403 Forbidden)")
    @WithMockUser(roles = "USER")
    void testGetAdminStatsAsUserForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/stats - Should reject unauthenticated request (401 Unauthorized)")
    void testGetAdminStatsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/admin/submissions - Should allow ADMIN to view global submissions")
    @WithMockUser(roles = "ADMIN")
    void testGetAdminSubmissionsAsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/admin/submissions - Should reject regular USER (403 Forbidden)")
    @WithMockUser(roles = "USER")
    void testGetAdminSubmissionsAsUserForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/submissions"))
                .andExpect(status().isForbidden());
    }
}
