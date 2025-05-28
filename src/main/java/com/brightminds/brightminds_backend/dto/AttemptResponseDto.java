package com.brightminds.brightminds_backend.dto;

import java.time.LocalDateTime;

public class AttemptResponseDto {
    private Long attemptId;
    private Long studentId;  // Only include the ID instead of full student object
    private Long gameId;     // Only include the ID instead of full game object
    private Long classroomGameId;  // Only include the ID instead of full classroomGame object
    private int score;
    private int expReward;
    private int timeTaken;
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;

    // Constructor
    public AttemptResponseDto() {}

    // Getters and Setters
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    
    public Long getClassroomGameId() { return classroomGameId; }
    public void setClassroomGameId(Long classroomGameId) { this.classroomGameId = classroomGameId; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getExpReward() { return expReward; }
    public void setExpReward(int expReward) { this.expReward = expReward; }
    
    public int getTimeTaken() { return timeTaken; }
    public void setTimeTaken(int timeTaken) { this.timeTaken = timeTaken; }
    
    public LocalDateTime getTimeStarted() { return timeStarted; }
    public void setTimeStarted(LocalDateTime timeStarted) { this.timeStarted = timeStarted; }
    
    public LocalDateTime getTimeFinished() { return timeFinished; }
    public void setTimeFinished(LocalDateTime timeFinished) { this.timeFinished = timeFinished; }
} 