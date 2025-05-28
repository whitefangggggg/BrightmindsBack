package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id") // This refers to the Game.activityId (the game template)
    private Game game;

    // MODIFICATION: Link Attempt directly to the specific ClassroomGame (assignment)
    @ManyToOne // Make optional=false if an attempt MUST always be tied to an assignment
    @JoinColumn(name = "classroom_game_id", nullable = true) // nullable=true allows attempts for non-assigned/playground games
    private ClassroomGame classroomGame;

    private int score;
    private int expReward;
    private int timeTaken; // in seconds
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;

    // Getters and Setters

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // Getter and Setter for classroomGame
    public ClassroomGame getClassroomGame() {
        return classroomGame;
    }

    public void setClassroomGame(ClassroomGame classroomGame) {
        this.classroomGame = classroomGame;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = expReward;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(LocalDateTime timeStarted) {
        this.timeStarted = timeStarted;
    }

    public LocalDateTime getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(LocalDateTime timeFinished) {
        this.timeFinished = timeFinished;
    }
}