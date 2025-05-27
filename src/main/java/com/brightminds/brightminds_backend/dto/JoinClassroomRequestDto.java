package com.brightminds.brightminds_backend.dto;

public class JoinClassroomRequestDto {
    private String joinCode;
    private Long studentId;

    public JoinClassroomRequestDto() {
    }

    public String getJoinCode() {
        return joinCode;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}