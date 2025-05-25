package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.Assignment;
import com.brightminds.brightminds_backend.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAssignmentsByClassroomId(Long classroomId) {
        return assignmentRepository.findByClassroomId(classroomId);
    }

    public List<Assignment> getAssignmentsByQuizId(Long quizId) {
        return assignmentRepository.findByQuizId(quizId);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}