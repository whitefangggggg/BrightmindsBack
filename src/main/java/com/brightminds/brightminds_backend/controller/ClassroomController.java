package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.JoinClassroomRequestDto;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
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

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody CreateClassroomRequestDto createClassroomRequestDto) {
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
    public ResponseEntity<Classroom> addStudentToClassroom(@PathVariable Long id, @RequestBody Student student) {
        Student studentToAdd = studentRepository.findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + student.getId()));
        return ResponseEntity.ok(classroomService.addStudentToClassroom(id, studentToAdd));
    }

    @PostMapping("/{id}/remove-student")
    public ResponseEntity<Classroom> removeStudentFromClassroom(@PathVariable Long id, @RequestBody Student student) {
        Student studentToRemove = studentRepository.findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + student.getId()));
        return ResponseEntity.ok(classroomService.removeStudentFromClassroom(id, studentToRemove));
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

    @PostMapping("/enroll")
    public ResponseEntity<Classroom> enrollInClassroom(@RequestBody JoinClassroomRequestDto joinRequest) {
        Student student = studentRepository.findById(joinRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + joinRequest.getStudentId()));
        Classroom classroom = classroomService.addStudentByJoinCode(joinRequest.getJoinCode(), student);
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam boolean isPremade) {
        ClassroomGame classroomGame = classroomService.assignGameToClassroom(id, gameId, deadline, isPremade);
        return ResponseEntity.ok(classroomGame);
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<List<ClassroomGame>> getGamesForClassroom(@PathVariable Long id) {
        List<ClassroomGame> classroomGames = classroomService.getGamesForClassroom(id);
        return ResponseEntity.ok(classroomGames);
    }

    @GetMapping("/playground/games")
    public ResponseEntity<List<ClassroomGame>> getPlaygroundGames() {
        List<ClassroomGame> playgroundGames = classroomService.getPlaygroundGames();
        return ResponseEntity.ok(playgroundGames);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsInClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
        return ResponseEntity.ok(classroom.getStudents());
    }
}