package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto; // Import the DTO
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // Modified to accept CreateClassroomRequestDto
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody CreateClassroomRequestDto createClassroomRequestDto) {
        // Call the service method that expects CreateClassroomRequestDto
        Classroom newClassroom = classroomService.createClassroom(createClassroomRequestDto);
        return ResponseEntity.ok(newClassroom);
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

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Classroom>> getClassroomsByTeacherId(@PathVariable Long teacherId) {
        List<Classroom> classrooms = classroomService.getClassroomsByTeacherId(teacherId);
        if (classrooms.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(classrooms);
    }

    @PostMapping("/{id}/add-student")
    public ResponseEntity<Classroom> addStudent(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.ok(classroomService.addStudentToClassroom(id, student));
    }

    @PostMapping("/{id}/remove-student")
    public ResponseEntity<Classroom> removeStudent(@PathVariable Long id, @RequestBody Student student) {
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

    @PostMapping("/join")
    public ResponseEntity<Classroom> joinClassroom(@RequestParam String joinCode, @RequestParam Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Classroom classroom = classroomService.addStudentByJoinCode(joinCode, student);
        return ResponseEntity.ok(classroom);
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<Student>> getLeaderboard(@PathVariable Long id) {
        List<Student> leaderboard = classroomService.getLeaderboard(id);
        return ResponseEntity.ok(leaderboard);
    }

    @PostMapping("/{id}/assign-game")
    public ResponseEntity<ClassroomGame> assignGameToClassroom(
            @PathVariable Long id,
            @RequestParam Long gameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime deadline,
            @RequestParam boolean isPremade) {
        ClassroomGame classroomGame = classroomService.assignGameToClassroom(id, gameId, deadline, isPremade);
        return ResponseEntity.ok(classroomGame);
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<List<ClassroomGame>> getGamesForClassroom(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getGamesForClassroom(id));
    }

    @GetMapping("/playground/games")
    public ResponseEntity<List<ClassroomGame>> getPlaygroundGames() {
        return ResponseEntity.ok(classroomService.getPlaygroundGames());
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsInClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroomById(id).orElseThrow();
        return ResponseEntity.ok(classroom.getStudents());
    }
}