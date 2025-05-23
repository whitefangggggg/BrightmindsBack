package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // "TEACHER" or "STUDENT"

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}