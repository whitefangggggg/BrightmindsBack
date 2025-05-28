package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        try {
            // Ensure game data is properly serialized
            if (game.getGameData() != null) {
                // Validate that gameData is valid JSON
                objectMapper.readTree(game.getGameData());
            }
            
            // Set default values if not provided
            if (game.getMaxScore() <= 0) {
                game.setMaxScore(100);
            }
            if (game.getMaxExp() <= 0) {
                game.setMaxExp(50);
            }

            // Handle createdBy field
            if (game.getCreatedBy() != null && game.getCreatedBy().getId() != null) {
                Teacher teacher = teacherRepository.findById(game.getCreatedBy().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
                game.setCreatedBy(teacher);
            }
            
            Game savedGame = gameRepository.save(game);
            return ResponseEntity.ok(savedGame);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @Valid @RequestBody Game updated) {
        return gameRepository.findById(id)
                .map(game -> {
                    game.setActivityName(updated.getActivityName());
                    game.setMaxScore(updated.getMaxScore());
                    game.setMaxExp(updated.getMaxExp());
                    game.setGameMode(updated.getGameMode());
                    game.setGameData(updated.getGameData());
                    
                    // Handle createdBy field update
                    if (updated.getCreatedBy() != null && updated.getCreatedBy().getId() != null) {
                        Teacher teacher = teacherRepository.findById(updated.getCreatedBy().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
                        game.setCreatedBy(teacher);
                    }
                    
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
} 