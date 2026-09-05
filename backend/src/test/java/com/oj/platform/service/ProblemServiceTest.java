package com.oj.platform.service;

import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.ProblemUpdateRequest;
import com.oj.platform.dto.response.ProblemResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.exception.BadRequestException;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.service.impl.ProblemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemServiceImpl problemService;

    private Problem sampleProblem;

    @BeforeEach
    void setUp() {
        sampleProblem = Problem.builder()
                .id(10L)
                .title("Valid Palindrome")
                .description("Determine if a string is a valid palindrome.")
                .difficulty(Difficulty.EASY)
                .category("Strings")
                .timeLimitMs(2000)
                .memoryLimitMb(256)
                .build();
    }

    @Test
    @DisplayName("createProblem() - Should create and return problem response")
    void testCreateProblemSuccess() {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Valid Palindrome")
                .description("Determine if a string is a valid palindrome.")
                .difficulty(Difficulty.EASY)
                .category("Strings")
                .build();

        when(problemRepository.existsByTitle("Valid Palindrome")).thenReturn(false);
        when(problemRepository.save(any(Problem.class))).thenReturn(sampleProblem);

        ProblemResponse response = problemService.createProblem(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Valid Palindrome");
        assertThat(response.getDifficulty()).isEqualTo(Difficulty.EASY);
        assertThat(response.getCategory()).isEqualTo("Strings");
    }

    @Test
    @DisplayName("createProblem() - Should throw BadRequestException on duplicate title")
    void testCreateProblemDuplicateTitle() {
        ProblemCreateRequest request = ProblemCreateRequest.builder()
                .title("Valid Palindrome")
                .description("Determine if a string is a valid palindrome.")
                .difficulty(Difficulty.EASY)
                .category("Strings")
                .build();

        when(problemRepository.existsByTitle("Valid Palindrome")).thenReturn(true);

        assertThatThrownBy(() -> problemService.createProblem(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Problem with title 'Valid Palindrome' already exists.");
    }

    @Test
    @DisplayName("getProblemById() - Should return problem by ID")
    void testGetProblemByIdSuccess() {
        when(problemRepository.findById(10L)).thenReturn(Optional.of(sampleProblem));

        ProblemResponse response = problemService.getProblemById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Valid Palindrome");
    }

    @Test
    @DisplayName("getProblemById() - Should throw ResourceNotFoundException when problem not found")
    void testGetProblemByIdNotFound() {
        when(problemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> problemService.getProblemById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Problem not found with id : '999'");
    }

    @Test
    @DisplayName("getAllCategories() - Should return distinct category list")
    void testGetAllCategories() {
        when(problemRepository.findDistinctCategories()).thenReturn(List.of("Arrays", "Dynamic Programming", "Strings"));

        List<String> categories = problemService.getAllCategories();

        assertThat(categories).hasSize(3);
        assertThat(categories).containsExactly("Arrays", "Dynamic Programming", "Strings");
    }

    @Test
    @DisplayName("deleteProblem() - Should delete problem when it exists")
    void testDeleteProblemSuccess() {
        when(problemRepository.existsById(10L)).thenReturn(true);

        problemService.deleteProblem(10L);

        verify(problemRepository).deleteById(10L);
    }
}
