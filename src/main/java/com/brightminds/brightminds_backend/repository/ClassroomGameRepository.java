package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.ClassroomGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.data.repository.query.Param; // Import Param
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassroomGameRepository extends JpaRepository<ClassroomGame, Long> {
    List<ClassroomGame> findByClassroomId(Long classroomId);
    List<ClassroomGame> findByGameIsPremadeTrue();

    // New method for fetching by classroom ID and the game's activity ID
    @Query("SELECT cg FROM ClassroomGame cg WHERE cg.classroom.id = :classroomId AND cg.game.activityId = :gameActivityId")
    List<ClassroomGame> findByClassroomIdAndGameActivityId(@Param("classroomId") Long classroomId, @Param("gameActivityId") Long gameActivityId);
}