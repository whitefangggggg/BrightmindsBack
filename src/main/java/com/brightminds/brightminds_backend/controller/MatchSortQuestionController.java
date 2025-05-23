package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.MatchSortQuestion;
import com.brightminds.brightminds_backend.service.MatchSortQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-sort-questions")
public class MatchSortQuestionController {
    private final MatchSortQuestionService matchSortQuestionService;

    @Autowired
    public MatchSortQuestionController(MatchSortQuestionService matchSortQuestionService) {
        this.matchSortQuestionService = matchSortQuestionService;
    }

    @GetMapping
    public List<MatchSortQuestion> getAllQuestions() {
        return matchSortQuestionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchSortQuestion> getQuestionById(@PathVariable Long id) {
        return matchSortQuestionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MatchSortQuestion createQuestion(@RequestBody MatchSortQuestion question) {
        return matchSortQuestionService.createQuestion(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        matchSortQuestionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}