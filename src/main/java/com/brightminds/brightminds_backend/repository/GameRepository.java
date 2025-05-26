package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
} 