package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {
    @Autowired
    private AttemptRepository attemptRepository;

    @GetMapping
    public List<Attempt> getAllAttempts() {
        return attemptRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attempt> getAttempt(@PathVariable Long id) {
        return attemptRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Attempt createAttempt(@RequestBody Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attempt> updateAttempt(@PathVariable Long id, @RequestBody Attempt updated) {
        return attemptRepository.findById(id)
                .map(attempt -> {
                    attempt.setScore(updated.getScore());
                    attempt.setExpReward(updated.getExpReward());
                    attempt.setTimeTaken(updated.getTimeTaken());
                    attempt.setTimeStarted(updated.getTimeStarted());
                    attempt.setTimeFinished(updated.getTimeFinished());
                    return ResponseEntity.ok(attemptRepository.save(attempt));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttempt(@PathVariable Long id) {
        if (attemptRepository.existsById(id)) {
            attemptRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
} 