package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
@Data
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id")
    private Game game;

    private int score;
    private int expReward;
    private int timeTaken; // in seconds
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;
} 