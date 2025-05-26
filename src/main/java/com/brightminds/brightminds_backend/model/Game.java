package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import lombok.Data;

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
} 