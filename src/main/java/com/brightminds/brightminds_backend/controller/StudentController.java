package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @PutMapping("/{id}/avatar")
    public ResponseEntity<Student> updateAvatar(@PathVariable Long id, @RequestParam String avatarImage) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setAvatarImage(avatarImage);
        studentRepository.save(student);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/{id}/classrooms")
    public List<Classroom> getClassroomsForStudent(@PathVariable Long id) {
        return classroomRepository.findAll().stream()
            .filter(c -> c.getStudents().stream().anyMatch(s -> s.getId().equals(id)))
            .toList();
    }
} 