package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @PutMapping("/{id}/avatar")
    public ResponseEntity<Student> updateAvatar(@PathVariable Long id, @RequestParam String avatarImage) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setAvatarImage(avatarImage);
        studentRepository.save(student);
        return ResponseEntity.ok(student);
    }
} 