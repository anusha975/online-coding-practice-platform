package com.oj.platform.judge;

import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Language;
import com.oj.platform.entity.Problem;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.entity.TestCase;
import com.oj.platform.judge.comparator.OutputComparator;
import com.oj.platform.judge.model.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CodeExecutionIntegrationTest {

    @Autowired
    private JudgeEngine judgeEngine;

    @Autowired
    private OutputComparator outputComparator;

    private Problem problem;
    private List<TestCase> testCases;

    @BeforeEach
    void setUp() {
        problem = Problem.builder()
                .id(100L)
                .title("Add Two Numbers")
                .description("Given two numbers a and b, return their sum.")
                .difficulty(Difficulty.EASY)
                .category("Math")
                .timeLimitMs(2000)
                .memoryLimitMb(256)
                .build();

        TestCase tc1 = TestCase.builder()
                .id(1L)
                .problem(problem)
                .input("2 3\n")
                .expectedOutput("5\n")
                .hidden(false)
                .build();

        TestCase tc2 = TestCase.builder()
                .id(2L)
                .problem(problem)
                .input("100 250\n")
                .expectedOutput("350\n")
                .hidden(true)
                .build();

        testCases = List.of(tc1, tc2);
    }

    // ==========================================
    // Java Execution Tests
    // ==========================================

    @Test
    @DisplayName("Java - Correct solution should return ACCEPTED")
    void testJavaAccepted() {
        String javaCode = """
                import java.util.Scanner;
                public class Solution {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        if (sc.hasNextInt()) {
                            int a = sc.nextInt();
                            int b = sc.nextInt();
                            System.out.println(a + b);
                        }
                    }
                }
                """;

        ExecutionResult result = judgeEngine.evaluate(Language.JAVA, javaCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(result.getPassedTestCases()).isEqualTo(2);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
    }

    @Test
    @DisplayName("Java - Wrong answer should return WRONG_ANSWER")
    void testJavaWrongAnswer() {
        String javaCode = """
                import java.util.Scanner;
                public class Solution {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        if (sc.hasNextInt()) {
                            int a = sc.nextInt();
                            int b = sc.nextInt();
                            System.out.println(a * b); // Bug: multiplication instead of addition
                        }
                    }
                }
                """;

        ExecutionResult result = judgeEngine.evaluate(Language.JAVA, javaCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
        assertThat(result.getPassedTestCases()).isEqualTo(0);
    }

    @Test
    @DisplayName("Java - Compilation error should return COMPILATION_ERROR")
    void testJavaCompilationError() {
        String javaCode = """
                public class Solution {
                    public static void main(String[] args) {
                        syntax error here !!!
                    }
                }
                """;

        ExecutionResult result = judgeEngine.evaluate(Language.JAVA, javaCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getErrorMessage()).isNotBlank();
        assertThat(result.getPassedTestCases()).isEqualTo(0);
    }

    @Test
    @DisplayName("Java - Runtime error (division by zero) should return RUNTIME_ERROR")
    void testJavaRuntimeError() {
        String javaCode = """
                import java.util.Scanner;
                public class Solution {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        int a = sc.nextInt();
                        int b = 0;
                        System.out.println(a / b); // Triggers ArithmeticException
                    }
                }
                """;

        ExecutionResult result = judgeEngine.evaluate(Language.JAVA, javaCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.RUNTIME_ERROR);
        assertThat(result.getErrorMessage()).contains("ArithmeticException");
    }

    @Test
    @DisplayName("Java - Infinite loop should be caught by timeout watchdog and return TIME_LIMIT_EXCEEDED")
    void testJavaTimeLimitExceeded() {
        problem.setTimeLimitMs(1000); // 1 second timeout

        String javaCode = """
                public class Solution {
                    public static void main(String[] args) {
                        while (true) {
                            // Infinite loop
                        }
                    }
                }
                """;

        ExecutionResult result = judgeEngine.evaluate(Language.JAVA, javaCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
        assertThat(result.getErrorMessage()).contains("Time Limit Exceeded");
    }

    // ==========================================
    // Python Execution Tests
    // ==========================================

    @Test
    @DisplayName("Python - Correct solution should return ACCEPTED")
    void testPythonAccepted() {
        String pyCode = """
import sys
tokens = sys.stdin.read().split()
if len(tokens) >= 2:
    print(int(tokens[0]) + int(tokens[1]))
""";

        ExecutionResult result = judgeEngine.evaluate(Language.PYTHON, pyCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(result.getPassedTestCases()).isEqualTo(2);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
    }

    @Test
    @DisplayName("Python - Wrong answer should return WRONG_ANSWER")
    void testPythonWrongAnswer() {
        String pyCode = """
import sys
tokens = sys.stdin.read().split()
if len(tokens) >= 2:
    print(int(tokens[0]) - int(tokens[1]))
""";

        ExecutionResult result = judgeEngine.evaluate(Language.PYTHON, pyCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
        assertThat(result.getPassedTestCases()).isEqualTo(0);
    }

    @Test
    @DisplayName("Python - Syntax error should return COMPILATION_ERROR")
    void testPythonSyntaxError() {
        String pyCode = """
def broken_function(:
    pass
""";

        ExecutionResult result = judgeEngine.evaluate(Language.PYTHON, pyCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getErrorMessage()).contains("SyntaxError");
    }

    @Test
    @DisplayName("Python - Runtime error should return RUNTIME_ERROR")
    void testPythonRuntimeError() {
        String pyCode = """
import sys
x = 10 / 0
""";

        ExecutionResult result = judgeEngine.evaluate(Language.PYTHON, pyCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.RUNTIME_ERROR);
        assertThat(result.getErrorMessage()).contains("ZeroDivisionError");
    }

    @Test
    @DisplayName("Python - Infinite loop should return TIME_LIMIT_EXCEEDED")
    void testPythonTimeLimitExceeded() {
        problem.setTimeLimitMs(1000);

        String pyCode = """
while True:
    pass
""";

        ExecutionResult result = judgeEngine.evaluate(Language.PYTHON, pyCode, problem, testCases);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.TIME_LIMIT_EXCEEDED);
        assertThat(result.getErrorMessage()).contains("Time Limit Exceeded");
    }

    // ==========================================
    // Output Comparator Unit Tests
    // ==========================================

    @Test
    @DisplayName("OutputComparator - Matches CRLF and LF with trailing spaces")
    void testOutputComparator() {
        String actual = "Hello World \r\n42   \r\n";
        String expected = "Hello World\n42\n";

        assertThat(outputComparator.compare(actual, expected)).isTrue();
    }
}
