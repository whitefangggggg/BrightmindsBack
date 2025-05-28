package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Reward;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.RewardRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.service.RewardService;
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

    @Autowired
    private RewardService rewardService;

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

    @PostMapping("/award-top-performers/{classroomId}")
    public ResponseEntity<String> awardTopPerformersBadges(@PathVariable Long classroomId) {
        logger.info("Awarding badges to top performers in classroom ID: {}", classroomId);
        try {
            rewardService.awardTopPerformersBadges(classroomId);
            return ResponseEntity.ok("Badges awarded successfully to top performers");
        } catch (Exception e) {
            logger.error("Error awarding badges: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error awarding badges: " + e.getMessage());
        }
    }
}