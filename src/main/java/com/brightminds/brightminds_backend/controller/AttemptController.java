package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private ClassroomGameRepository classroomGameRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GameRepository gameRepository;

    // Student submits a score for a classroom-assigned game
    @PostMapping("/submit")
    public ResponseEntity<?> submitScore(@RequestParam Long studentId,
                                         @RequestParam Long classroomGameId,
                                         @RequestParam int score,
                                         @RequestParam(required = false) Integer timeTaken) {
        ClassroomGame classroomGame = classroomGameRepository.findById(classroomGameId).orElseThrow();
        if (classroomGame.getDeadline() != null && LocalDateTime.now().isAfter(classroomGame.getDeadline())) {
            return ResponseEntity.badRequest().body("Deadline has passed for this game.");
        }
        Attempt attempt = new Attempt();
        attempt.setStudent(studentRepository.findById(studentId).orElseThrow());
        attempt.setGame(classroomGame.getGame());
        attempt.setScore(score);
        attempt.setTimeTaken(timeTaken != null ? timeTaken : 0);
        attempt.setTimeStarted(null);
        attempt.setTimeFinished(LocalDateTime.now());
        attemptRepository.save(attempt);
        return ResponseEntity.ok(attempt);
    }

    // Student submits a score for a playground game
    @PostMapping("/playground/submit")
    public ResponseEntity<?> submitPlaygroundScore(@RequestParam Long studentId,
                                                   @RequestParam Long gameId,
                                                   @RequestParam int score,
                                                   @RequestParam(required = false) Integer timeTaken) {
        Attempt attempt = new Attempt();
        attempt.setStudent(studentRepository.findById(studentId).orElseThrow());
        attempt.setGame(gameRepository.findById(gameId).orElseThrow());
        attempt.setScore(score);
        attempt.setTimeTaken(timeTaken != null ? timeTaken : 0);
        attempt.setTimeStarted(null);
        attempt.setTimeFinished(LocalDateTime.now());
        attemptRepository.save(attempt);
        return ResponseEntity.ok(attempt);
    }

    // Get all attempts for a student
    @GetMapping("/student/{studentId}")
    public List<Attempt> getAttemptsByStudent(@PathVariable Long studentId) {
        return attemptRepository.findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .toList();
    }

    // Get all attempts for a game
    @GetMapping("/game/{gameId}")
    public List<Attempt> getAttemptsByGame(@PathVariable Long gameId) {
        return attemptRepository.findAll().stream()
                .filter(a -> a.getGame().getActivityId().equals(gameId))
                .toList();
    }
} 