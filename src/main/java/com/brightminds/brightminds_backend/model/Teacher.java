package com.brightminds.brightminds_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teachers")
@Data
public class Teacher extends User {
    private String profilePhoto; // URL or path to the uploaded profile photo
} 