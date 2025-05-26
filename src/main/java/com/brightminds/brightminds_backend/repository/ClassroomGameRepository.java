package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.ClassroomGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassroomGameRepository extends JpaRepository<ClassroomGame, Long> {
    List<ClassroomGame> findByClassroomId(Long classroomId);
    List<ClassroomGame> findByGameIsPremadeTrue();
} 