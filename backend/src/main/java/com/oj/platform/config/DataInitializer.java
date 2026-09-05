package com.oj.platform.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.platform.dto.dataset.ProblemSeedDto;
import com.oj.platform.dto.dataset.TestCaseSeedDto;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.TestCase;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Role;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Automatically seeds the database with initial admin/user credentials
 * and 200+ LeetCode-style algorithmic challenges across 13 core DSA domains.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedDsaProblems();
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

    private void seedDsaProblems() {
        long currentCount = problemRepository.count();
        if (currentCount >= 200) {
            log.info("Problem catalog already contains {} challenges. Skipping dataset seeding.", currentCount);
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("data/dsa_problems.json");
            if (!resource.exists()) {
                log.warn("Dataset file 'data/dsa_problems.json' not found on classpath. Skipping bulk problem seeding.");
                return;
            }

            log.info("Loading 200+ comprehensive DSA problem dataset from data/dsa_problems.json...");
            try (InputStream is = resource.getInputStream()) {
                List<ProblemSeedDto> seedDtos = objectMapper.readValue(is, new TypeReference<>() {});
                log.info("Parsed {} problems from dataset. Inserting missing problems into database...", seedDtos.size());

                Set<String> existingTitles = problemRepository.findAll().stream()
                        .map(Problem::getTitle)
                        .collect(Collectors.toSet());

                List<Problem> newProblems = new ArrayList<>();
                List<List<TestCaseSeedDto>> testCaseSeedsForNewProblems = new ArrayList<>();

                for (ProblemSeedDto dto : seedDtos) {
                    if (!existingTitles.contains(dto.getTitle())) {
                        Problem problem = Problem.builder()
                                .title(dto.getTitle())
                                .description(dto.getDescription())
                                .difficulty(dto.getDifficulty())
                                .category(dto.getCategory())
                                .constraints(dto.getConstraints())
                                .inputFormat(dto.getInputFormat())
                                .outputFormat(dto.getOutputFormat())
                                .sampleInput(dto.getSampleInput())
                                .sampleOutput(dto.getSampleOutput())
                                .timeLimitMs(dto.getTimeLimitMs() != null ? dto.getTimeLimitMs() : 2000)
                                .memoryLimitMb(dto.getMemoryLimitMb() != null ? dto.getMemoryLimitMb() : 256)
                                .build();

                        newProblems.add(problem);
                        testCaseSeedsForNewProblems.add(dto.getTestCases());
                    }
                }

                if (!newProblems.isEmpty()) {
                    List<Problem> savedProblems = problemRepository.saveAll(newProblems);
                    List<TestCase> allTestCasesToSave = new ArrayList<>();

                    for (int i = 0; i < savedProblems.size(); i++) {
                        Problem savedProblem = savedProblems.get(i);
                        List<TestCaseSeedDto> tcDtos = testCaseSeedsForNewProblems.get(i);

                        if (tcDtos != null && !tcDtos.isEmpty()) {
                            for (TestCaseSeedDto tcDto : tcDtos) {
                                allTestCasesToSave.add(
                                        TestCase.builder()
                                                .problem(savedProblem)
                                                .input(tcDto.getInput())
                                                .expectedOutput(tcDto.getExpectedOutput())
                                                .hidden(tcDto.isHidden())
                                                .build()
                                );
                            }
                        }
                    }

                    if (!allTestCasesToSave.isEmpty()) {
                        testCaseRepository.saveAll(allTestCasesToSave);
                    }

                    log.info("Successfully seeded {} new DSA problems with {} associated test cases! Total problems in database: {}",
                            savedProblems.size(), allTestCasesToSave.size(), problemRepository.count());
                }
            }
        } catch (Exception e) {
            log.error("Failed to seed 200+ DSA problem dataset: {}", e.getMessage(), e);
        }
    }
}
