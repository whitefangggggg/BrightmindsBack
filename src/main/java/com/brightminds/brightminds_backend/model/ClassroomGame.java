package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "classroom_games")
@Data
public class ClassroomGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    private LocalDateTime deadline;
    private boolean isPremade;
} 