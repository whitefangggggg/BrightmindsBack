package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.Reward;
import com.brightminds.brightminds_backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByStudent(Student student);
}