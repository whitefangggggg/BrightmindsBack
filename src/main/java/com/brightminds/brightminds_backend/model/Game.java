package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Lob;

@Entity
@Table(name = "games")
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false, unique = true)
    private String activityName;

    private int maxScore;
    private int maxExp;

    private boolean isPremade;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Teacher createdBy;

    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @Lob
    private String gameData; // JSON or other format for game-specific data
} 