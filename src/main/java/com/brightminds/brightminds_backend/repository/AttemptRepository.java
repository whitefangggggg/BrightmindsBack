package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.ClassroomGame; // Import ClassroomGame
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

    // Method needed to find all attempts associated with a specific ClassroomGame
    // This is used when deleting a classroom to clear out dependent attempts.
    List<Attempt> findByClassroomGame(ClassroomGame classroomGame);

    // Alternatively, if you prefer to query by the ID of ClassroomGame directly:
    // List<Attempt> findByClassroomGame_Id(Long classroomGameId);
    // Note: Spring Data JPA can derive queries from method names.
    // "findByClassroomGame_Id" would look for attempts where the 'id' field of the 'classroomGame' property matches.
    // Use findByClassroomGame if you already have the ClassroomGame object.
}