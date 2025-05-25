package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.User;
import com.brightminds.brightminds_backend.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        return ResponseEntity.ok(classroomService.createClassroom(classroom));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Classroom> getAllClassrooms() {
        return classroomService.getAllClassrooms();
    }

    @PostMapping("/{id}/add-student")
    public ResponseEntity<Classroom> addStudent(@PathVariable Long id, @RequestBody User student) {
        return ResponseEntity.ok(classroomService.addStudentToClassroom(id, student));
    }

    @PostMapping("/{id}/remove-student")
    public ResponseEntity<Classroom> removeStudent(@PathVariable Long id, @RequestBody User student) {
        return ResponseEntity.ok(classroomService.removeStudentFromClassroom(id, student));
    }

    @GetMapping("/join/{joinCode}")
    public ResponseEntity<Classroom> getClassroomByJoinCode(@PathVariable String joinCode) {
        Classroom classroom = classroomService.findByJoinCode(joinCode);
        if (classroom != null) {
            return ResponseEntity.ok(classroom);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}