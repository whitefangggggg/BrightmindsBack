package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.dto.TeacherProfileUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;

    @PutMapping("/{id}/profile")
    public ResponseEntity<Teacher> updateProfile(@PathVariable Long id, @RequestBody TeacherProfileUpdateDto profileUpdate) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow();
        
        // Update teacher fields
        teacher.setFirstName(profileUpdate.getFirstName());
        teacher.setLastName(profileUpdate.getLastName());
        teacher.setEmail(profileUpdate.getEmail());
        teacher.setProfilePhoto(profileUpdate.getProfilePhoto());
        
        teacherRepository.save(teacher);
        return ResponseEntity.ok(teacher);
    }
} 