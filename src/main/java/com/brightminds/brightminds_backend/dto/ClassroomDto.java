package com.brightminds.brightminds_backend.dto;

import java.util.List;

public class ClassroomDto {
    private Long id;
    private String name;
    private String description;
    private Long teacherId;
    private List<Long> studentIds;
    private String joinCode;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }
}