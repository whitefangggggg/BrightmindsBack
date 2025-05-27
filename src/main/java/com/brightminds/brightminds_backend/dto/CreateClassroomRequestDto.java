package com.brightminds.brightminds_backend.dto;

public class CreateClassroomRequestDto {
    private String name;
    private String description;
    private Long teacherId;

    public CreateClassroomRequestDto() {
    }

    public CreateClassroomRequestDto(String name, String description, Long teacherId) {
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}