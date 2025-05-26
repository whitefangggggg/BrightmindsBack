package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
} 