package com.oj.platform.controller;

import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiChatResponse;
import com.oj.platform.dto.ai.AiCodeReviewRequest;
import com.oj.platform.dto.ai.AiCodeReviewResponse;
import com.oj.platform.dto.ai.AiHintRequest;
import com.oj.platform.dto.ai.AiHintResponse;
import com.oj.platform.dto.ai.AiMentorRequest;
import com.oj.platform.dto.ai.AiMentorResponse;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for AI-powered coding assistant, progressive hints, code review, and RAG-grounded mentor interactions.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(
            @Valid @RequestBody AiChatRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = (currentUser != null) ? currentUser.getId() : 0L;
        log.info("Received AI mentor chat request from user ID: {}", userId);

        AiChatResponse response = aiService.chat(request, userId);
        return ResponseEntity.ok(ApiResponse.success("AI response generated successfully", response));
    }

    @PostMapping("/hint")
    public ResponseEntity<ApiResponse<AiHintResponse>> getHint(
            @Valid @RequestBody AiHintRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = (currentUser != null) ? currentUser.getId() : 0L;
        log.info("Received AI progressive hint request (Level {}) from user ID: {} for problem: {}",
                request.getRequestedHintLevel(), userId, request.getProblemTitle());

        AiHintResponse response = aiService.generateHint(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Progressive hint generated successfully", response));
    }

    @PostMapping("/code-review")
    public ResponseEntity<ApiResponse<AiCodeReviewResponse>> reviewCode(
            @Valid @RequestBody AiCodeReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = (currentUser != null) ? currentUser.getId() : 0L;
        log.info("Received AI code review request from user ID: {} for problem: {}, verdict: {}",
                userId, request.getProblemTitle(), request.getVerdict());

        AiCodeReviewResponse response = aiService.reviewCode(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Code review completed successfully", response));
    }

    @PostMapping("/mentor")
    public ResponseEntity<ApiResponse<AiMentorResponse>> mentor(
            @Valid @RequestBody AiMentorRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = (currentUser != null) ? currentUser.getId() : 0L;
        log.info("Received RAG AI Mentor question from user ID: {}, topic: {}, query: '{}'",
                userId, request.getTopic(), request.getQuestion());

        AiMentorResponse response = aiService.mentor(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Grounded mentor response generated successfully", response));
    }
}
