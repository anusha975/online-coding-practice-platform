package com.oj.platform.service.impl;

import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.ProblemUpdateRequest;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.ProblemResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.exception.BadRequestException;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.specification.ProblemSpecification;
import com.oj.platform.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ProblemService with pagination, dynamic criteria filters, and CRUD logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public ProblemResponse createProblem(ProblemCreateRequest request) {
        log.info("Creating new problem with title: {}", request.getTitle());

        // 1. Uniqueness check for problem title
        if (problemRepository.existsByTitle(request.getTitle().trim())) {
            throw new BadRequestException(String.format("Problem with title '%s' already exists.", request.getTitle()));
        }

        // 2. Build Entity
        Problem problem = Problem.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .difficulty(request.getDifficulty())
                .category(request.getCategory().trim())
                .constraints(request.getConstraints())
                .inputFormat(request.getInputFormat())
                .outputFormat(request.getOutputFormat())
                .sampleInput(request.getSampleInput())
                .sampleOutput(request.getSampleOutput())
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 2000)
                .memoryLimitMb(request.getMemoryLimitMb() != null ? request.getMemoryLimitMb() : 256)
                .build();

        // 3. Persist and return DTO
        Problem saved = problemRepository.save(problem);
        log.info("Problem created successfully with ID: {}", saved.getId());

        return ProblemResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(Long id) {
        log.info("Fetching problem by ID: {}", id);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));

        return ProblemResponse.fromEntity(problem);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProblemResponse> getProblems(
            String search,
            Difficulty difficulty,
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        log.info("Fetching problems - page: {}, size: {}, search: {}, difficulty: {}, category: {}, sortBy: {}, sortDir: {}",
                page, size, search, difficulty, category, sortBy, sortDir);

        // 1. Setup Sorting & Pagination
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        // 2. Apply Dynamic Filter Specification
        Specification<Problem> spec = ProblemSpecification.filterProblems(search, difficulty, category);

        // 3. Execute Query
        Page<Problem> problemPage = problemRepository.findAll(spec, pageable);

        // 4. Map to DTO page response
        Page<ProblemResponse> dtoPage = problemPage.map(ProblemResponse::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<String> getAllCategories() {
        log.info("Fetching distinct problem categories from database");
        return problemRepository.findDistinctCategories();
    }

    @Override
    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemUpdateRequest request) {
        log.info("Updating problem ID: {}", id);

        // 1. Find existing problem
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));

        // 2. Check title uniqueness if title changed
        if (problemRepository.existsByTitleAndIdNot(request.getTitle().trim(), id)) {
            throw new BadRequestException(String.format("Problem with title '%s' already exists.", request.getTitle()));
        }

        // 3. Update entity fields
        problem.setTitle(request.getTitle().trim());
        problem.setDescription(request.getDescription().trim());
        problem.setDifficulty(request.getDifficulty());
        problem.setCategory(request.getCategory().trim());
        problem.setConstraints(request.getConstraints());
        problem.setInputFormat(request.getInputFormat());
        problem.setOutputFormat(request.getOutputFormat());
        problem.setSampleInput(request.getSampleInput());
        problem.setSampleOutput(request.getSampleOutput());
        if (request.getTimeLimitMs() != null) {
            problem.setTimeLimitMs(request.getTimeLimitMs());
        }
        if (request.getMemoryLimitMb() != null) {
            problem.setMemoryLimitMb(request.getMemoryLimitMb());
        }

        Problem updated = problemRepository.save(problem);
        log.info("Problem ID: {} updated successfully.", updated.getId());

        return ProblemResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteProblem(Long id) {
        log.info("Deleting problem ID: {}", id);

        if (!problemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Problem", "id", id);
        }

        problemRepository.deleteById(id);
        log.info("Problem ID: {} deleted successfully.", id);
    }
}
