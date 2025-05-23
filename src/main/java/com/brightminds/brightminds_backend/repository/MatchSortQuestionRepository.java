package com.brightminds.brightminds_backend.repository;

import com.brightminds.brightminds_backend.model.MatchSortQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSortQuestionRepository extends JpaRepository<MatchSortQuestion, Long> {
}