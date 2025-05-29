package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.ClassroomScore;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClassroomScoreRepository extends JpaRepository<ClassroomScore, Long> {
    Optional<ClassroomScore> findByClassroomAndStudent(Classroom classroom, Student student);
    List<ClassroomScore> findByClassroomOrderByTotalScoreDesc(Classroom classroom);
}