package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.ClassroomGame; // Ensure this import is correct
import com.brightminds.brightminds_backend.model.Student; // Added if findByStudentAndClassroomGame is defined
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    // Find attempts by student ID and the specific classroom game assignment ID
    List<Attempt> findByStudentIdAndClassroomGameId(Long studentId, Long classroomGameId);

    // Find attempts by student ID and the general game (activity) ID
    // This might be useful for other statistics, but ClassroomService currently uses the one above for game-specific leaderboards.
    List<Attempt> findByStudentIdAndGameActivityId(Long studentId, Long gameActivityId);

    // Count attempts for a specific student and assigned game
    long countByStudentIdAndClassroomGameId(Long studentId, Long classroomGameId);

    // Method needed to find all attempts associated with a specific ClassroomGame
    // This is used when deleting a classroom or a specific ClassroomGame to clear out dependent attempts.
    List<Attempt> findByClassroomGame(ClassroomGame classroomGame);

    // Optional: If you prefer to also have a method that takes Student and ClassroomGame objects directly
    // List<Attempt> findByStudentAndClassroomGame(Student student, ClassroomGame classroomGame);
    // Spring Data JPA can derive this query. If you add this, ClassroomService
    // would not need to pass student.getId() and assignment.getId() to findByStudentIdAndClassroomGameId.
}