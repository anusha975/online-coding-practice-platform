package com.oj.platform.config;

import com.oj.platform.entity.Problem;
import com.oj.platform.entity.TestCase;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Role;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Automatically seeds the database with initial admin/user credentials
 * and starter algorithmic challenges if the tables are empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedProblems();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Seeding initial users and administrative accounts...");

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();

            userRepository.saveAll(List.of(admin, user));
            log.info("Initialized default users: admin/admin123 and user/user123");
        }
    }

    private void seedProblems() {
        if (problemRepository.count() == 0) {
            log.info("Seeding starter problem catalog and test cases...");

            // Problem 1: Two Sum
            Problem twoSum = Problem.builder()
                    .title("Two Sum")
                    .description("Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nOutput the two 0-based indices separated by a space in ascending order.")
                    .difficulty(Difficulty.EASY)
                    .category("Arrays & Hashing")
                    .constraints("2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nExactly one valid answer exists.")
                    .inputFormat("First line contains integer N (size of array).\nSecond line contains N space-separated integers.\nThird line contains integer target.")
                    .outputFormat("Print the two 0-based indices separated by a space.")
                    .sampleInput("4\n2 7 11 15\n9")
                    .sampleOutput("0 1")
                    .timeLimitMs(2000)
                    .memoryLimitMb(256)
                    .build();
            twoSum = problemRepository.save(twoSum);

            testCaseRepository.saveAll(List.of(
                    TestCase.builder().problem(twoSum).input("4\n2 7 11 15\n9").expectedOutput("0 1").hidden(false).build(),
                    TestCase.builder().problem(twoSum).input("3\n3 2 4\n6").expectedOutput("1 2").hidden(false).build(),
                    TestCase.builder().problem(twoSum).input("2\n3 3\n6").expectedOutput("0 1").hidden(true).build(),
                    TestCase.builder().problem(twoSum).input("5\n1 5 3 7 9\n12").expectedOutput("1 3").hidden(true).build()
            ));

            // Problem 2: Valid Palindrome
            Problem palindrome = Problem.builder()
                    .title("Valid Palindrome")
                    .description("A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward.\n\nGiven a string `s`, print `true` if it is a palindrome, or `false` otherwise.")
                    .difficulty(Difficulty.EASY)
                    .category("Strings")
                    .constraints("1 <= s.length <= 2 * 10^5\ns consists only of printable ASCII characters.")
                    .inputFormat("Single line containing the string s.")
                    .outputFormat("Print 'true' if palindrome, 'false' otherwise.")
                    .sampleInput("A man, a plan, a canal: Panama")
                    .sampleOutput("true")
                    .timeLimitMs(2000)
                    .memoryLimitMb(256)
                    .build();
            palindrome = problemRepository.save(palindrome);

            testCaseRepository.saveAll(List.of(
                    TestCase.builder().problem(palindrome).input("A man, a plan, a canal: Panama").expectedOutput("true").hidden(false).build(),
                    TestCase.builder().problem(palindrome).input("race a car").expectedOutput("false").hidden(false).build(),
                    TestCase.builder().problem(palindrome).input(" ").expectedOutput("true").hidden(true).build(),
                    TestCase.builder().problem(palindrome).input("0P").expectedOutput("false").hidden(true).build()
            ));

            // Problem 3: Reverse String
            Problem reverseStr = Problem.builder()
                    .title("Reverse String")
                    .description("Write a program that reverses a string. The input string is given as a single line.\n\nPrint the reversed string.")
                    .difficulty(Difficulty.EASY)
                    .category("Strings")
                    .constraints("1 <= s.length <= 10^5")
                    .inputFormat("Single line containing the string.")
                    .outputFormat("Print the reversed string.")
                    .sampleInput("hello")
                    .sampleOutput("olleh")
                    .timeLimitMs(1000)
                    .memoryLimitMb(128)
                    .build();
            reverseStr = problemRepository.save(reverseStr);

            testCaseRepository.saveAll(List.of(
                    TestCase.builder().problem(reverseStr).input("hello").expectedOutput("olleh").hidden(false).build(),
                    TestCase.builder().problem(reverseStr).input("Hannah").expectedOutput("hannaH").hidden(false).build(),
                    TestCase.builder().problem(reverseStr).input("CodeForge").expectedOutput("egroFedoC").hidden(true).build()
            ));

            // Problem 4: Fibonacci Number
            Problem fibonacci = Problem.builder()
                    .title("Fibonacci Number")
                    .description("The Fibonacci numbers, commonly denoted `F(n)` form a sequence, called the Fibonacci sequence, such that each number is the sum of the two preceding ones, starting from `0` and `1`.\n\nGiven `n`, calculate `F(n)`.")
                    .difficulty(Difficulty.EASY)
                    .category("Dynamic Programming")
                    .constraints("0 <= n <= 30")
                    .inputFormat("Single integer n.")
                    .outputFormat("Print F(n).")
                    .sampleInput("4")
                    .sampleOutput("3")
                    .timeLimitMs(1000)
                    .memoryLimitMb(128)
                    .build();
            fibonacci = problemRepository.save(fibonacci);

            testCaseRepository.saveAll(List.of(
                    TestCase.builder().problem(fibonacci).input("2").expectedOutput("1").hidden(false).build(),
                    TestCase.builder().problem(fibonacci).input("3").expectedOutput("2").hidden(false).build(),
                    TestCase.builder().problem(fibonacci).input("4").expectedOutput("3").hidden(false).build(),
                    TestCase.builder().problem(fibonacci).input("10").expectedOutput("55").hidden(true).build(),
                    TestCase.builder().problem(fibonacci).input("0").expectedOutput("0").hidden(true).build()
            ));

            // Problem 5: Container With Most Water
            Problem waterContainer = Problem.builder()
                    .title("Container With Most Water")
                    .description("You are given an integer array `height` of length `n`. There are `n` vertical lines drawn such that the two endpoints of the `i-th` line are `(i, 0)` and `(i, height[i])`.\n\nFind two lines that together with the x-axis form a container, such that the container contains the most water.\n\nReturn the maximum amount of water a container can store.")
                    .difficulty(Difficulty.MEDIUM)
                    .category("Two Pointers")
                    .constraints("n == height.length\n2 <= n <= 10^5\n0 <= height[i] <= 10^4")
                    .inputFormat("First line contains integer n.\nSecond line contains n space-separated integers representing heights.")
                    .outputFormat("Print the maximum area of water that can be contained.")
                    .sampleInput("9\n1 8 6 2 5 4 8 3 7")
                    .sampleOutput("49")
                    .timeLimitMs(2000)
                    .memoryLimitMb(256)
                    .build();
            waterContainer = problemRepository.save(waterContainer);

            testCaseRepository.saveAll(List.of(
                    TestCase.builder().problem(waterContainer).input("9\n1 8 6 2 5 4 8 3 7").expectedOutput("49").hidden(false).build(),
                    TestCase.builder().problem(waterContainer).input("2\n1 1").expectedOutput("1").hidden(false).build(),
                    TestCase.builder().problem(waterContainer).input("4\n4 3 2 1 4").expectedOutput("16").hidden(true).build()
            ));

            log.info("Initialized 5 coding problems with sample and hidden test cases.");
        }
    }
}
