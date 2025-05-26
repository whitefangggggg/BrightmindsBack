package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;

    @PutMapping("/{id}/profile-photo")
    public ResponseEntity<Teacher> updateProfilePhoto(@PathVariable Long id, @RequestParam String profilePhoto) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        teacher.setProfilePhoto(profilePhoto);
        teacherRepository.save(teacher);
        return ResponseEntity.ok(teacher);
    }
} 