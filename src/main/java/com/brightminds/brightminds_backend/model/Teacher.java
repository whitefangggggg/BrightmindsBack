package com.brightminds.brightminds_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teachers")
@Data
public class Teacher extends User {
    // Add teacher-specific fields here in the future
} 