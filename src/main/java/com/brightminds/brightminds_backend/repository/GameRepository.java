package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByIsPremadeTrue();
    
    @Query("SELECT g FROM Game g WHERE g.createdBy.id = :teacherId OR g.isPremade = true")
    List<Game> findByTeacherIdOrPremade(@Param("teacherId") Long teacherId);
} 