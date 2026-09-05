package com.oj.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiChatResponse;
import com.oj.platform.dto.ai.AiHintRequest;
import com.oj.platform.dto.ai.AiHintResponse;
import com.oj.platform.service.impl.OpenAiCompatibleAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private RestClient restClient;

    private OpenAiCompatibleAiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new OpenAiCompatibleAiService(restClient, new ObjectMapper());
        ReflectionTestUtils.setField(aiService, "aiEnabled", true);
        ReflectionTestUtils.setField(aiService, "apiKey", "");
        ReflectionTestUtils.setField(aiService, "apiUrl", "https://api.openai.com/v1/chat/completions");
        ReflectionTestUtils.setField(aiService, "model", "gpt-4o-mini");
    }

    @Test
    @DisplayName("chat() - Should generate pedagogical hint for hint questions")
    void testChatHintGeneration() {
        AiChatRequest request = AiChatRequest.builder()
                .question("Give me a hint for this problem")
                .problemTitle("Container With Most Water")
                .problemDifficulty("MEDIUM")
                .language("JAVA")
                .build();

        AiChatResponse response = aiService.chat(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getSuggestedAction()).isEqualTo("HINT");
        assertThat(response.getAnswer()).contains("Mentor Hint for **Container With Most Water**");
    }

    @Test
    @DisplayName("chat() - Should analyze time complexity when requested")
    void testChatComplexityAnalysis() {
        AiChatRequest request = AiChatRequest.builder()
                .question("What is the time complexity of my code?")
                .problemTitle("Two Sum")
                .userCode("for (int i = 0; i < n; i++) { for (int j = i+1; j < n; j++) {} }")
                .language("JAVA")
                .build();

        AiChatResponse response = aiService.chat(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getSuggestedAction()).isEqualTo("TIME_COMPLEXITY");
        assertThat(response.getAnswer()).contains("Complexity Analysis Guide");
    }

    @Test
    @DisplayName("chat() - Should suggest edge cases when requested")
    void testChatEdgeCases() {
        AiChatRequest request = AiChatRequest.builder()
                .question("What edge cases should I test?")
                .problemTitle("Merge Intervals")
                .build();

        AiChatResponse response = aiService.chat(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getSuggestedAction()).isEqualTo("EDGE_CASES");
        assertThat(response.getAnswer()).contains("Critical Edge Cases to Check");
    }

    @Test
    @DisplayName("generateHint() - Level 1 should return conceptual direction without code")
    void testGenerateHintLevel1() {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDescription("Given an array of integers nums...")
                .requestedHintLevel(1)
                .mode("HINT")
                .build();

        AiHintResponse response = aiService.generateHint(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getHintLevel()).isEqualTo(1);
        assertThat(response.getTitle()).contains("Level 1");
        assertThat(response.getContent()).contains("Hash Map");
        assertThat(response.getNextAction()).isEqualTo("NEXT_HINT");
        assertThat(response.getHintsUsedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("generateHint() - Level 2 & 3 should increment hintsUsedCount and return targeted guidance")
    void testGenerateHintLevel2And3() {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Valid Palindrome")
                .problemCategory("Strings")
                .problemDescription("A string is palindrome if...")
                .previousHints(List.of("Hint 1: Two pointers"))
                .requestedHintLevel(2)
                .mode("HINT")
                .build();

        AiHintResponse response = aiService.generateHint(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getHintLevel()).isEqualTo(2);
        assertThat(response.getHintsUsedCount()).isEqualTo(2);
        assertThat(response.getContent()).contains("Problematic Logic");

        AiHintRequest level3Req = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Valid Palindrome")
                .problemCategory("Strings")
                .problemDescription("A string is palindrome if...")
                .previousHints(List.of("Hint 1", "Hint 2"))
                .requestedHintLevel(3)
                .mode("HINT")
                .build();

        AiHintResponse level3Res = aiService.generateHint(level3Req, 1L);
        assertThat(level3Res.getHintLevel()).isEqualTo(3);
        assertThat(level3Res.getNextAction()).isEqualTo("SHOW_SOLUTION");
        assertThat(level3Res.getHintsUsedCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("generateHint() - Level 4 should provide full solution walkthrough")
    void testGenerateHintLevel4Solution() {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemCategory("Arrays")
                .problemDescription("Given an array of integers nums...")
                .programmingLanguage("JAVA")
                .requestedHintLevel(4)
                .mode("SOLUTION")
                .build();

        AiHintResponse response = aiService.generateHint(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getHintLevel()).isEqualTo(4);
        assertThat(response.getContent()).contains("Time Complexity").contains("Solution");
        assertThat(response.getNextAction()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("generateHint() - Should return diagnostic when mode is MISTAKE")
    void testGenerateHintMistakeMode() {
        AiHintRequest request = AiHintRequest.builder()
                .problemId(1L)
                .problemTitle("Two Sum")
                .problemDescription("Given an array of integers nums...")
                .verdict("WRONG_ANSWER")
                .errorMessage("Expected [0, 1], got [0, 0]")
                .mode("MISTAKE")
                .build();

        AiHintResponse response = aiService.generateHint(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).contains("Mistake");
        assertThat(response.getContent()).contains("WRONG_ANSWER").contains("Expected [0, 1]");
    }
}
