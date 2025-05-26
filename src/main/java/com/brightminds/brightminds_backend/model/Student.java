package com.brightminds.brightminds_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;
import jakarta.persistence.OneToMany;
import com.brightminds.brightminds_backend.model.Attempt;

@Entity
@Table(name = "students")
@Data
public class Student extends User {
    // Add student-specific fields here in the future
    private int expAmount;
    private int studentLevel;

    @OneToMany(mappedBy = "student")
    private List<Reward> rewards;

    @OneToMany(mappedBy = "student")
    private List<Attempt> attempts;
} 