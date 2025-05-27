package com.brightminds.brightminds_backend.exception;

public class ClassroomAlreadyJoinedException extends RuntimeException {
    public ClassroomAlreadyJoinedException(String message) {
        super(message);
    }
} 