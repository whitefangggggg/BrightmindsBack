package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Classroom findByJoinCode(String joinCode);
    List<Classroom> findByTeacherId(Long teacherId);
}