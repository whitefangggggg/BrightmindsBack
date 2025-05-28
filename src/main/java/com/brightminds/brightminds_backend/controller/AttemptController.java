package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.dto.StudentGameAttemptRequestDto;
import com.brightminds.brightminds_backend.dto.AttemptResponseDto;
import com.brightminds.brightminds_backend.model.Attempt;
// Keep existing model imports if used by other endpoints
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
// import com.brightminds.brightminds_backend.repository.ClassroomGameRepository; // Will be used in Service
import com.brightminds.brightminds_backend.service.AttemptService; // Import new/updated service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime; // Keep for playground endpoint
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attempts") // Changed from "/api/attempts" to "/api/game-attempts" in my previous proposal, but sticking to your existing "/api/attempts"
public class AttemptController {

    @Autowired
    private AttemptService attemptService; // Use the service

    // Autowire repositories directly ONLY IF other endpoints still use them directly
    // Otherwise, all repository access should ideally go through services
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GameRepository gameRepository;
    // @Autowired
    // private ClassroomGameRepository classroomGameRepository; // Moved to AttemptService

    private AttemptResponseDto convertToDto(Attempt attempt) {
        AttemptResponseDto dto = new AttemptResponseDto();
        dto.setAttemptId(attempt.getAttemptId());
        dto.setStudentId(attempt.getStudent().getId());
        dto.setGameId(attempt.getGame().getActivityId());
        if (attempt.getClassroomGame() != null) {
            dto.setClassroomGameId(attempt.getClassroomGame().getId());
        }
        dto.setScore(attempt.getScore());
        dto.setExpReward(attempt.getExpReward());
        dto.setTimeTaken(attempt.getTimeTaken());
        dto.setTimeStarted(attempt.getTimeStarted());
        dto.setTimeFinished(attempt.getTimeFinished());
        return dto;
    }

    /**
     * Student submits a score and other attempt details for a classroom-assigned game.
     * This endpoint now uses a RequestBody with a DTO.
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitAssignedGameAttempt(@RequestBody StudentGameAttemptRequestDto attemptDto) {
        try {
            Attempt savedAttempt = attemptService.recordAttempt(attemptDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedAttempt));
        } catch (IllegalArgumentException e) { // For known validation issues from service
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) { // For other specific errors like "Max attempts reached"
            // Log the full error for debugging on the server
            System.err.println("Error submitting attempt: " + e.getMessage());
            // e.printStackTrace(); // Consider using a proper logger

            if (e.getMessage() != null && e.getMessage().contains("Maximum number of attempts")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
            }
            if (e.getMessage() != null && e.getMessage().contains("Deadline has passed")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
            // Generic error for other runtime issues
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to record attempt: " + e.getMessage(), e);
        }
    }

    /**
     * Student submits a score for a playground (non-assigned) game.
     * This can remain as is or also be converted to use a DTO if desired for consistency.
     * For now, keeping it with RequestParams as per your existing code.
     */
    @PostMapping("/playground/submit")
    public ResponseEntity<?> submitPlaygroundScore(@RequestParam Long studentId,
                                                   @RequestParam Long gameId, // This is Game.activityId
                                                   @RequestParam int score,
                                                   @RequestParam(required = false) Integer timeTaken,
                                                   @RequestParam(required = false) Integer expGained) { // Added expGained here too
        try {
            // This logic could also be moved to AttemptService for consistency
            Attempt attempt = new Attempt();
            attempt.setStudent(studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId)));
            attempt.setGame(gameRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + gameId)));
            attempt.setScore(score);
            attempt.setTimeTaken(timeTaken != null ? timeTaken : 0);
            attempt.setExpReward(expGained != null ? expGained : (int)(score * 0.5)); // Example default EXP
            // attempt.setTimeStarted(null); // Or set appropriately
            attempt.setTimeFinished(LocalDateTime.now());

            Attempt savedAttempt = attemptRepository.save(attempt);

            // Update student's total EXP for playground games too
            Student student = attempt.getStudent();
            student.setExpAmount(student.getExpAmount() + attempt.getExpReward());
            studentRepository.save(student);

            return ResponseEntity.ok(convertToDto(savedAttempt));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error submitting playground score: " + e.getMessage());
            // e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", e);
        }
    }

    // Get all attempts for a student (remains the same)
    @GetMapping("/student/{studentId}")
    public List<AttemptResponseDto> getAttemptsByStudent(@PathVariable Long studentId) {
        // Consider moving this logic to AttemptService if not already
        return attemptRepository.findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get all attempts for a game (remains the same)
    @GetMapping("/game/{gameId}")
    public List<AttemptResponseDto> getAttemptsByGame(@PathVariable Long gameId) {
        // Consider moving this logic to AttemptService if not already
        return attemptRepository.findAll().stream()
                .filter(a -> a.getGame().getActivityId().equals(gameId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Optional: New endpoint as suggested in my AttemptService to get attempts for a specific assignment
    @GetMapping("/assignment/{assignedGameId}/student/{studentId}")
    public ResponseEntity<List<AttemptResponseDto>> getAttemptsForAssignmentByStudent(
            @PathVariable Long assignedGameId,
            @PathVariable Long studentId) {
        try {
            List<Attempt> attempts = attemptService.getAttemptsByStudentAndAssignedGame(studentId, assignedGameId);
            List<AttemptResponseDto> dtos = attempts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve attempts: " + e.getMessage(), e);
        }
    }
}