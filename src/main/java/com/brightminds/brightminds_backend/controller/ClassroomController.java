package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.JoinClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.LeaderboardEntryDto;
import com.brightminds.brightminds_backend.dto.UpdateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.AssignedGameResponseDto; // Import the DTO
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.service.ClassroomService;
import com.brightminds.brightminds_backend.exception.ClassroomAlreadyJoinedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // For mapping list to DTO list

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody CreateClassroomRequestDto createClassroomRequestDto) {
        try {
            Classroom newClassroom = classroomService.createClassroom(createClassroomRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newClassroom);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage, e);
            } else if (errorMessage.contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage, e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage, e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(
            @PathVariable Long id,
            @RequestBody UpdateClassroomRequestDto updateClassroomRequestDto) {
        try {
            Classroom updatedClassroom = classroomService.updateClassroom(id, updateClassroomRequestDto);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Classroom not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating classroom: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Classroom not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting classroom: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Classroom>> getClassroomsByTeacherId(@PathVariable Long teacherId) {
        List<Classroom> classrooms = classroomService.getClassroomsByTeacherId(teacherId);
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsInClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classroom not found with id: " + id));
        return ResponseEntity.ok(classroom.getStudents());
    }

    @PostMapping("/enroll")
    public ResponseEntity<Classroom> enrollInClassroom(@RequestBody JoinClassroomRequestDto joinRequest) {
        try {
            Student student = studentRepository.findById(joinRequest.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + joinRequest.getStudentId()));
            Classroom classroom = classroomService.addStudentByJoinCode(joinRequest.getJoinCode(), student);
            return ResponseEntity.ok(classroom);
        } catch (ClassroomAlreadyJoinedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable Long id) {
        try {
            List<LeaderboardEntryDto> leaderboard = classroomService.getLeaderboard(id);
            return ResponseEntity.ok(leaderboard);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping("/{id}/assign-game")
    public ResponseEntity<AssignedGameResponseDto> assignGameToClassroom(
            @PathVariable Long id,
            @RequestParam Long gameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam boolean isPremade,
            @RequestParam(required = false) Integer maxAttempts) {
        try {
            // Ensure your classroomService.assignGameToClassroom method signature matches:
            // ClassroomGame assignGameToClassroom(Long classroomId, Long gameId, LocalDateTime deadline, boolean isPremadeAssignment, Integer maxAttempts);
            ClassroomGame classroomGame = classroomService.assignGameToClassroom(id, gameId, deadline, isPremade, maxAttempts);
            AssignedGameResponseDto responseDto = AssignedGameResponseDto.from(classroomGame);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<List<AssignedGameResponseDto>> getGamesForClassroom(@PathVariable Long id) {
        if (!classroomService.getClassroomById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ClassroomGame> classroomGames = classroomService.getGamesForClassroom(id);
        List<AssignedGameResponseDto> responseDtos = classroomGames.stream()
                .map(AssignedGameResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<Classroom> addStudentToClassroomById(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        try {
            Student studentToAdd = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
            Classroom updatedClassroom = classroomService.addStudentToClassroom(classroomId, studentToAdd);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<Classroom> removeStudentFromClassroomById(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        try {
            Student studentToRemove = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
            Classroom updatedClassroom = classroomService.removeStudentFromClassroom(classroomId, studentToRemove);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/playground/games")
    public ResponseEntity<List<ClassroomGame>> getPlaygroundGames() {
        // This likely also needs to return a DTO if ClassroomGame causes serialization issues here
        List<ClassroomGame> playgroundGames = classroomService.getPlaygroundGames();
        return ResponseEntity.ok(playgroundGames);
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