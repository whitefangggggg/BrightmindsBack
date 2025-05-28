package com.brightminds.brightminds_backend.dto;

// This DTO matches the structure of 'CreateStudentGameAttemptDTO' from frontend types
public class StudentGameAttemptRequestDto {
    private String studentId; // User ID of the student (frontend might send User.id as string)
    private String assignedGameId; // ID of the ClassroomGame (the specific assignment)
    private String gameId; // ID of the actual Game entity (Game.activityId)
    private int score;
    private Integer expGained; // Optional: frontend can calculate or backend can
    private Integer timeTakenSeconds; // Optional

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getAssignedGameId() { return assignedGameId; }
    public void setAssignedGameId(String assignedGameId) { this.assignedGameId = assignedGameId; }
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Integer getExpGained() { return expGained; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
}