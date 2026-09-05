package com.oj.platform.service;

import com.oj.platform.dto.request.TestCaseCreateRequest;
import com.oj.platform.dto.response.TestCaseAdminResponse;
import com.oj.platform.dto.response.TestCaseSampleResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.TestCase;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.service.impl.TestCaseServiceImpl;
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
class TestCaseServiceTest {

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private TestCaseServiceImpl testCaseService;

    private Problem sampleProblem;
    private TestCase samplePublicTestCase;
    private TestCase sampleHiddenTestCase;

    @BeforeEach
    void setUp() {
        sampleProblem = Problem.builder()
                .id(1L)
                .title("Two Sum")
                .build();

        samplePublicTestCase = TestCase.builder()
                .id(10L)
                .problem(sampleProblem)
                .input("2 7 11 15\n9")
                .expectedOutput("0 1")
                .hidden(false)
                .build();

        sampleHiddenTestCase = TestCase.builder()
                .id(11L)
                .problem(sampleProblem)
                .input("3 3\n6")
                .expectedOutput("0 1")
                .hidden(true)
                .build();
    }

    @Test
    @DisplayName("createTestCase() - Should save and return test case admin response")
    void testCreateTestCaseSuccess() {
        TestCaseCreateRequest request = TestCaseCreateRequest.builder()
                .input("2 7 11 15\n9")
                .expectedOutput("0 1")
                .hidden(true)
                .build();

        when(problemRepository.findById(1L)).thenReturn(Optional.of(sampleProblem));
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(sampleHiddenTestCase);

        TestCaseAdminResponse response = testCaseService.createTestCase(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(11L);
        assertThat(response.isHidden()).isTrue();
    }

    @Test
    @DisplayName("getSampleTestCases() - Should strictly return only non-hidden test cases")
    void testGetSampleTestCasesOnlyNonHidden() {
        when(problemRepository.existsById(1L)).thenReturn(true);
        when(testCaseRepository.findByProblemIdAndHiddenFalse(1L)).thenReturn(List.of(samplePublicTestCase));

        List<TestCaseSampleResponse> samples = testCaseService.getSampleTestCases(1L);

        assertThat(samples).hasSize(1);
        assertThat(samples.get(0).getId()).isEqualTo(10L);
        assertThat(samples.get(0).getInput()).isEqualTo("2 7 11 15\n9");
    }

    @Test
    @DisplayName("getSampleTestCases() - Should throw ResourceNotFoundException when problem does not exist")
    void testGetSampleTestCasesProblemNotFound() {
        when(problemRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> testCaseService.getSampleTestCases(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Problem not found with id : '999'");
    }

    @Test
    @DisplayName("deleteTestCase() - Should delete test case when found")
    void testDeleteTestCaseSuccess() {
        when(testCaseRepository.existsById(10L)).thenReturn(true);

        testCaseService.deleteTestCase(10L);

        verify(testCaseRepository).deleteById(10L);
    }
}
