package com.brightminds.brightminds_backend.dto;

public class UpdateClassroomRequestDto {
    private String name;
    private String description;

    // Constructors, Getters, and Setters

    public UpdateClassroomRequestDto() {
    }

    public UpdateClassroomRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}