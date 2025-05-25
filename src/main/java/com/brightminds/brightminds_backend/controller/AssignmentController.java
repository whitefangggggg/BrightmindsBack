package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Assignment;
import com.brightminds.brightminds_backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.createAssignment(assignment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/classroom/{classroomId}")
    public List<Assignment> getAssignmentsByClassroom(@PathVariable Long classroomId) {
        return assignmentService.getAssignmentsByClassroomId(classroomId);
    }

    @GetMapping("/quiz/{quizId}")
    public List<Assignment> getAssignmentsByQuiz(@PathVariable Long quizId) {
        return assignmentService.getAssignmentsByQuizId(quizId);
    }

    @GetMapping
    public List<Assignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }
}