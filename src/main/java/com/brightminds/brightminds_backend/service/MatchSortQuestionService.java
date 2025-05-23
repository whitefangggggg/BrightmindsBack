package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.MatchSortQuestion;
import com.brightminds.brightminds_backend.repository.MatchSortQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchSortQuestionService {
    private final MatchSortQuestionRepository matchSortQuestionRepository;

    @Autowired
    public MatchSortQuestionService(MatchSortQuestionRepository matchSortQuestionRepository) {
        this.matchSortQuestionRepository = matchSortQuestionRepository;
    }

    public List<MatchSortQuestion> getAllQuestions() {
        return matchSortQuestionRepository.findAll();
    }

    public Optional<MatchSortQuestion> getQuestionById(Long id) {
        return matchSortQuestionRepository.findById(id);
    }

    public MatchSortQuestion createQuestion(MatchSortQuestion question) {
        return matchSortQuestionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        matchSortQuestionRepository.deleteById(id);
    }
}