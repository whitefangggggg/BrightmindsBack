package com.brightminds.brightminds_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "games", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"activity_name", "game_mode"})
})
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @NotBlank(message = "Activity name is required")
    @Column(nullable = false)
    private String activityName;

    // New field
    @Column(length = 1000) // Optional: define length for description
    private String description;

    // New field
    @Column(length = 255) // Optional: define length for subject
    private String subject;

    @Min(value = 0, message = "Max score must be positive")
    private int maxScore;

    @Min(value = 0, message = "Max experience must be positive")
    private int maxExp;

    private boolean isPremade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Teacher createdBy;

    @NotNull(message = "Game mode is required")
    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String gameData; // JSON or other format for game-specific data

    // Getters and Setters

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public boolean isPremade() {
        return isPremade;
    }

    public void setPremade(boolean premade) {
        isPremade = premade;
    }

    @JsonIgnore
    public Teacher getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Teacher createdBy) {
        this.createdBy = createdBy;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public String getGameData() {
        return gameData;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }
}