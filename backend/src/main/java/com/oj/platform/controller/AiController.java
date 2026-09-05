package com.oj.platform.controller;

import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiChatResponse;
import com.oj.platform.dto.ai.AiHintRequest;
import com.oj.platform.dto.ai.AiHintResponse;
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
 * REST Controller for AI-powered coding assistant, progressive hints, and pedagogical mentor interactions.
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
}
