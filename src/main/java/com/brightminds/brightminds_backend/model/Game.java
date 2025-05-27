package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;

import jakarta.persistence.Lob;

@Entity
@Table(name = "games")
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