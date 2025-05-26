package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByIsPremadeTrue();
} 