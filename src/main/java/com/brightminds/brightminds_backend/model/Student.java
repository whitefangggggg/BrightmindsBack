package com.brightminds.brightminds_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "students")
@Data
public class Student extends User {
    // Add student-specific fields here in the future
} 