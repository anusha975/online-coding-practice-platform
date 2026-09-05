package com.oj.platform.service;

import com.oj.platform.dto.request.SubmissionCreateRequest;
import com.oj.platform.dto.response.SubmissionResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.Submission;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.Role;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.judge.worker.SubmissionWorker;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.impl.SubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private SubmissionWorker submissionWorker;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    private User sampleUser;
    private Problem sampleProblem;
    private Submission sampleSubmission;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("coder_joe")
                .email("joe@example.com")
                .role(Role.USER)
                .build();

        sampleProblem = Problem.builder()
                .id(5L)
                .title("Two Sum")
                .build();

        sampleSubmission = Submission.builder()
                .id(100L)
                .user(sampleUser)
                .problem(sampleProblem)
                .language(Language.JAVA)
                .sourceCode("class Solution {}")
                .status(SubmissionStatus.PENDING)
                .passedTestCases(0)
                .totalTestCases(3)
                .build();

        userPrincipal = UserPrincipal.create(sampleUser);
    }

    @Test
    @DisplayName("createSubmission() - Should create PENDING submission and queue async execution")
    void testCreateSubmissionSuccess() {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(5L)
                .language(Language.JAVA)
                .sourceCode("class Solution {}")
                .build();

        when(problemRepository.findById(5L)).thenReturn(Optional.of(sampleProblem));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(testCaseRepository.countByProblemId(5L)).thenReturn(3L);
        when(submissionRepository.save(any(Submission.class))).thenReturn(sampleSubmission);

        SubmissionResponse response = submissionService.createSubmission(userPrincipal, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(SubmissionStatus.PENDING);
        assertThat(response.getLanguage()).isEqualTo(Language.JAVA);
        assertThat(response.getTotalTestCases()).isEqualTo(3);

        verify(submissionWorker).processSubmissionAsync(100L);
    }

    @Test
    @DisplayName("createSubmission() - Should throw ResourceNotFoundException when problem does not exist")
    void testCreateSubmissionProblemNotFound() {
        SubmissionCreateRequest request = SubmissionCreateRequest.builder()
                .problemId(999L)
                .language(Language.PYTHON)
                .sourceCode("print(1)")
                .build();

        when(problemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.createSubmission(userPrincipal, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Problem not found with id : '999'");
    }

    @Test
    @DisplayName("getSubmissionById() - Should allow owner to retrieve submission")
    void testGetSubmissionByIdAsOwner() {
        when(submissionRepository.findById(100L)).thenReturn(Optional.of(sampleSubmission));

        SubmissionResponse response = submissionService.getSubmissionById(100L, userPrincipal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("getSubmissionById() - Should deny other users (403 Access Denied)")
    void testGetSubmissionByIdOtherUserForbidden() {
        User otherUser = User.builder()
                .id(2L)
                .username("hacker")
                .email("hacker@example.com")
                .role(Role.USER)
                .build();
        UserPrincipal otherPrincipal = UserPrincipal.create(otherUser);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(sampleSubmission));

        assertThatThrownBy(() -> submissionService.getSubmissionById(100L, otherPrincipal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission to view this submission");
    }

    @Test
    @DisplayName("getSubmissionById() - Should allow ADMIN to view any submission")
    void testGetSubmissionByIdAsAdmin() {
        User adminUser = User.builder()
                .id(99L)
                .username("admin")
                .email("admin@platform.com")
                .role(Role.ADMIN)
                .build();
        UserPrincipal adminPrincipal = UserPrincipal.create(adminUser);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(sampleSubmission));

        SubmissionResponse response = submissionService.getSubmissionById(100L, adminPrincipal);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }
}
