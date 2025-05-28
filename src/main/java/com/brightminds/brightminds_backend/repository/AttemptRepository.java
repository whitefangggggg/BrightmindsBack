package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    // Find attempts by student ID and the specific classroom game assignment ID
    List<Attempt> findByStudentIdAndClassroomGameId(Long studentId, Long classroomGameId);

    // Find attempts by student ID and the general game (activity) ID
    List<Attempt> findByStudentIdAndGameActivityId(Long studentId, Long gameActivityId);

    // Count attempts for a specific student and assigned game
    long countByStudentIdAndClassroomGameId(Long studentId, Long classroomGameId);
}