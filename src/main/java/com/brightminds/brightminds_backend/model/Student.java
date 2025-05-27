package com.brightminds.brightminds_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import jakarta.persistence.OneToMany;
import com.brightminds.brightminds_backend.model.Attempt;

@Entity
@Table(name = "students")
public class Student extends User {
    // Add student-specific fields here in the future
    private int expAmount;
    private int studentLevel;
    private String avatarImage; // URL or path to the chosen avatar image

    @OneToMany(mappedBy = "student")
    private List<Reward> rewards;

    @OneToMany(mappedBy = "student")
    private List<Attempt> attempts;

    public int getExpAmount() { return expAmount; }
    public void setExpAmount(int expAmount) { this.expAmount = expAmount; }
    public int getStudentLevel() { return studentLevel; }
    public void setStudentLevel(int studentLevel) { this.studentLevel = studentLevel; }
    public String getAvatarImage() { return avatarImage; }
    public void setAvatarImage(String avatarImage) { this.avatarImage = avatarImage; }
    public List<Reward> getRewards() { return rewards; }
    public void setRewards(List<Reward> rewards) { this.rewards = rewards; }
    public List<Attempt> getAttempts() { return attempts; }
    public void setAttempts(List<Attempt> attempts) { this.attempts = attempts; }
} 