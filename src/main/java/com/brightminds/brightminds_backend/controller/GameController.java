package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @GetMapping
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Game createGame(@RequestBody Game game) {
        return gameRepository.save(game);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game updated) {
        return gameRepository.findById(id)
                .map(game -> {
                    game.setActivityName(updated.getActivityName());
                    game.setMaxScore(updated.getMaxScore());
                    game.setMaxExp(updated.getMaxExp());
                    return ResponseEntity.ok(gameRepository.save(game));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/premade")
    public List<Game> getPremadeGames() {
        return gameRepository.findByIsPremadeTrue();
    }

    @GetMapping("/by-teacher/{teacherId}")
    public List<Game> getGamesByTeacher(@PathVariable Long teacherId) {
        return gameRepository.findAll().stream()
            .filter(g -> g.getCreatedBy() != null && g.getCreatedBy().getId().equals(teacherId))
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return gameRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
} 