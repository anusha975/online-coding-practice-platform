package com.oj.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiHintRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/ai/chat - Should return AI mentor hint for valid request")
    void testAiChatHint() throws Exception {
        AiChatRequest request = AiChatRequest.builder()
                .question("Can you give me a hint on how to solve this?")
                .problemTitle("Two Sum")
                .problemDifficulty("EASY")
                .problemDescription("Given an array of integers nums and target...")
                .userCode("class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{}; } }")
                .language("JAVA")
                .build();

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.answer", notNullValue()))
                .andExpect(jsonPath("$.data.suggestedAction", is("HINT")))
                .andExpect(jsonPath("$.data.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/ai/chat - Should return debugging help when verdict is WRONG_ANSWER")
    @WithMockUser(username = "coder1")
    void testAiChatDebugging() throws Exception {
        AiChatRequest request = AiChatRequest.builder()
                .question("Why is my code failing test cases?")
                .problemTitle("Valid Palindrome")
                .userCode("def isPalindrome(s): return s == s[::-1]")
                .language("PYTHON")
                .verdict("WRONG_ANSWER")
                .errorMessage("Output: false, Expected: true")
                .build();

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.answer", containsString("Wrong Answer")))
                .andExpect(jsonPath("$.data.suggestedAction", is("DEBUG_ERROR")));
    }

    @Test
    @DisplayName("POST /api/ai/chat - Should return 400 Bad Request if question is blank")
    void testAiChatBlankQuestion() throws Exception {
        AiChatRequest request = AiChatRequest.builder()
                .question("   ")
                .problemTitle("Two Sum")
                .build();

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.question", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/ai/hint - Should return Level 1 conceptual hint without code")
    void testProgressiveHintLevel1() throws Exception {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDifficulty("EASY")
                .problemDescription("Given an array of integers nums and an integer target...")
                .programmingLanguage("JAVA")
                .requestedHintLevel(1)
                .mode("HINT")
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.hintLevel", is(1)))
                .andExpect(jsonPath("$.data.title", containsString("Level 1")))
                .andExpect(jsonPath("$.data.content", containsString("Conceptual Direction")))
                .andExpect(jsonPath("$.data.whyThisHelps", notNullValue()))
                .andExpect(jsonPath("$.data.nextAction", is("NEXT_HINT")))
                .andExpect(jsonPath("$.data.hintsUsedCount", is(1)));
    }

    @Test
    @DisplayName("POST /api/ai/hint - Should return Level 2 & 3 progressive hints with previous context")
    void testProgressiveHintLevel2And3() throws Exception {
        AiHintRequest level2Request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDifficulty("EASY")
                .problemDescription("Given an array of integers nums and an integer target...")
                .userCode("for (int i=0; i<nums.length; i++) {}")
                .programmingLanguage("JAVA")
                .previousHints(List.of("Level 1: Use a HashMap for O(1) lookups"))
                .requestedHintLevel(2)
                .mode("HINT")
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level2Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.hintLevel", is(2)))
                .andExpect(jsonPath("$.data.title", containsString("Level 2")))
                .andExpect(jsonPath("$.data.hintsUsedCount", is(2)));

        AiHintRequest level3Request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDifficulty("EASY")
                .problemDescription("Given an array of integers nums and an integer target...")
                .programmingLanguage("JAVA")
                .previousHints(List.of("Level 1 hint", "Level 2 hint"))
                .requestedHintLevel(3)
                .mode("HINT")
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(level3Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hintLevel", is(3)))
                .andExpect(jsonPath("$.data.nextAction", is("SHOW_SOLUTION")))
                .andExpect(jsonPath("$.data.hintsUsedCount", is(3)));
    }

    @Test
    @DisplayName("POST /api/ai/hint - Should return Level 4 full solution explanation when requested")
    void testProgressiveHintLevel4Solution() throws Exception {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDifficulty("EASY")
                .problemDescription("Given an array of integers nums and an integer target...")
                .programmingLanguage("PYTHON")
                .requestedHintLevel(4)
                .mode("SOLUTION")
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.hintLevel", is(4)))
                .andExpect(jsonPath("$.data.title", containsString("Level 4")))
                .andExpect(jsonPath("$.data.content", containsString("Time Complexity")))
                .andExpect(jsonPath("$.data.nextAction", is("COMPLETED")));
    }

    @Test
    @DisplayName("POST /api/ai/hint - Should analyze mistakes when mode is MISTAKE")
    void testExplainMistakeMode() throws Exception {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(2L)
                .problemTitle("Valid Palindrome")
                .problemDescription("A phrase is a palindrome if...")
                .userCode("def isPalindrome(s): return False")
                .programmingLanguage("PYTHON")
                .verdict("WRONG_ANSWER")
                .errorMessage("Failed on input: 'racecar'")
                .mode("MISTAKE")
                .requestedHintLevel(2)
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", containsString("Mistake")))
                .andExpect(jsonPath("$.data.content", containsString("WRONG_ANSWER")));
    }

    @Test
    @DisplayName("POST /api/ai/hint - Should return 400 Bad Request if problemDescription is blank")
    void testBlankProblemDescription() throws Exception {
        AiHintRequest request = AiHintRequest.builder()
                .problemTitle("Two Sum")
                .problemDescription("   ")
                .requestedHintLevel(1)
                .build();

        mockMvc.perform(post("/api/ai/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.problemDescription", notNullValue()));
    }
}
