package com.brightminds.brightminds_backend.dto;

public class LeaderboardEntryDto {
    private Long studentId;
    private String firstName;
    private String lastName;
    private int expAmount;
    private String avatarImage;
    // Rank will be determined by the order in the list from the service

    public LeaderboardEntryDto(Long studentId, String firstName, String lastName, int expAmount, String avatarImage) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expAmount = expAmount;
        this.avatarImage = avatarImage;
    }

    // Getters (and Setters if you need them, though constructor is often enough for DTOs)
    public Long getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getExpAmount() { return expAmount; }
    public String getAvatarImage() { return avatarImage; }
}