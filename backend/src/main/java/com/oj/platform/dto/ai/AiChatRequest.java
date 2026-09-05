package com.oj.platform.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for AI coding mentor interactions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatRequest {

    @NotBlank(message = "Question cannot be blank")
    @Size(max = 2000, message = "Question cannot exceed 2000 characters")
    private String question;

    private Long problemId;

    private String problemTitle;

    private String problemDescription;

    private String problemDifficulty;

    @Size(max = 50000, message = "User code cannot exceed 50000 characters")
    private String userCode;

    private String language;

    private String errorMessage;

    private String verdict;
}
