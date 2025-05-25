package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Classroom findByJoinCode(String joinCode);
}