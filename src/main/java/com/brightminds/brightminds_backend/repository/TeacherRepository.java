package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
} 