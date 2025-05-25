package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassroomId(Long classroomId);
    List<Assignment> findByQuizId(Long quizId);
}