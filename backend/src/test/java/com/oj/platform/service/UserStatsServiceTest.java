package com.oj.platform.service;

import com.oj.platform.dto.response.UserStatsResponse;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Role;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.service.impl.UserStatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStatsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private UserStatsServiceImpl userStatsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("algo_master")
                .email("algo@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("getUserStats() - Should compute and return aggregated metrics")
    void testGetUserStatsSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(submissionRepository.countByUserId(1L)).thenReturn(10L);
        when(submissionRepository.countByUserIdAndStatus(1L, SubmissionStatus.ACCEPTED)).thenReturn(8L);
        when(submissionRepository.countDistinctProblemsByUserIdAndStatus(1L, SubmissionStatus.ACCEPTED)).thenReturn(5L);
        when(submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(1L, SubmissionStatus.ACCEPTED, Difficulty.EASY)).thenReturn(2L);
        when(submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(1L, SubmissionStatus.ACCEPTED, Difficulty.MEDIUM)).thenReturn(2L);
        when(submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(1L, SubmissionStatus.ACCEPTED, Difficulty.HARD)).thenReturn(1L);

        when(problemRepository.count()).thenReturn(20L);
        when(problemRepository.countByDifficulty(Difficulty.EASY)).thenReturn(8L);
        when(problemRepository.countByDifficulty(Difficulty.MEDIUM)).thenReturn(8L);
        when(problemRepository.countByDifficulty(Difficulty.HARD)).thenReturn(4L);

        UserStatsResponse response = userStatsService.getUserStats(1L);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("algo_master");
        assertThat(response.getTotalSubmissions()).isEqualTo(10L);
        assertThat(response.getAcceptedSubmissions()).isEqualTo(8L);
        assertThat(response.getAcceptanceRate()).isEqualTo(80.0);
        assertThat(response.getTotalSolved()).isEqualTo(5L);
        assertThat(response.getEasySolved()).isEqualTo(2L);
        assertThat(response.getMediumSolved()).isEqualTo(2L);
        assertThat(response.getHardSolved()).isEqualTo(1L);
        assertThat(response.getTotalProblems()).isEqualTo(20L);
    }

    @Test
    @DisplayName("getUserStats() - Should throw ResourceNotFoundException for unknown user")
    void testGetUserStatsUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userStatsService.getUserStats(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id : '999'");
    }
}
