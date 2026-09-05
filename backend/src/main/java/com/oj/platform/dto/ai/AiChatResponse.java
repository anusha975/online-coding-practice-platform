package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned by the AI coding mentor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatResponse {

    private String answer;

    private String suggestedAction;

    private String model;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
