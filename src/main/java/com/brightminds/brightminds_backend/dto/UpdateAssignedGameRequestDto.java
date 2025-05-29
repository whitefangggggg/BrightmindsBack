package com.brightminds.brightminds_backend.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.FutureOrPresent; // Optional: if you want to validate the deadline
import jakarta.validation.constraints.Min; // Optional: if you want to validate maxAttempts

public class UpdateAssignedGameRequestDto {

    @FutureOrPresent(message = "Deadline must be in the present or future.") // Optional validation
    private LocalDateTime deadline;

    @Min(value = 1, message = "Max attempts must be at least 1, or null for unlimited.") // Optional validation
    private Integer maxAttempts; // Use Integer to allow null for unlimited attempts

    // Default constructor
    public UpdateAssignedGameRequestDto() {
    }

    // Constructor with all fields
    public UpdateAssignedGameRequestDto(LocalDateTime deadline, Integer maxAttempts) {
        this.deadline = deadline;
        this.maxAttempts = maxAttempts;
    }

    // Getters
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    // Setters
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}