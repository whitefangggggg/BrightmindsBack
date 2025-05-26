package com.brightminds.brightminds_backend.dto;

public class StudentPerformanceDto {
    private Long studentId;
    private String studentName;
    private Long classroomId;
    private int score;
    private String completedAt;

    // Getters and setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}