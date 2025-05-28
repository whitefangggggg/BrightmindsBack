package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Reward;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.RewardRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<Reward>> getRewardsByStudent(@RequestParam Long studentId) {
        logger.info("Retrieving badges for student ID: {}", studentId);
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> {
                        logger.error("Student not found with ID: {}", studentId);
                        return new RuntimeException("Student not found with ID: " + studentId);
                    });
            List<Reward> rewards = rewardRepository.findByStudent(student);
            logger.debug("Found {} badges for student ID {}", rewards.size(), studentId);
            return ResponseEntity.ok(rewards);
        } catch (Exception e) {
            logger.error("Error retrieving badges: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}