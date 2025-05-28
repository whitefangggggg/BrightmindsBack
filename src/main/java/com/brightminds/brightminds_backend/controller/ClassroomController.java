package com.brightminds.brightminds_backend.controller;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.JoinClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.LeaderboardEntryDto;
import com.brightminds.brightminds_backend.dto.UpdateClassroomRequestDto;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.repository.StudentRepository;
// TeacherRepository might be needed if you perform direct teacher checks here
// import com.brightminds.brightminds_backend.repository.TeacherRepository;
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

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentRepository studentRepository;

    // Endpoint to create a new classroom
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody CreateClassroomRequestDto createClassroomRequestDto) {
        try {
            Classroom newClassroom = classroomService.createClassroom(createClassroomRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newClassroom); // Return 201 Created
        } catch (RuntimeException e) {
            // Consider more specific exception handling or a @ControllerAdvice
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // Endpoint to get a specific classroom by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to update an existing classroom
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(
            @PathVariable Long id,
            @RequestBody UpdateClassroomRequestDto updateClassroomRequestDto) {
        try {
            // Optional: Add authorization check here to ensure only the teacher of this classroom can update it.
            Classroom updatedClassroom = classroomService.updateClassroom(id, updateClassroomRequestDto);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Classroom not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating classroom: " + e.getMessage(), e);
        }
    }

    // Endpoint to delete a classroom
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        try {
            // Optional: Add authorization check here (e.g., ensure the requester is the teacher of this classroom).
            classroomService.deleteClassroom(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content is standard for successful DELETE
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Classroom not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            // For other errors during deletion (e.g., database issues not caught by service)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting classroom: " + e.getMessage(), e);
        }
    }


    // Endpoint to get all classrooms (consider pagination for many classrooms)
    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    // Endpoint to get all classrooms taught by a specific teacher
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Classroom>> getClassroomsByTeacherId(@PathVariable Long teacherId) {
        // Consider security: ensure only authorized users can access this, or the logged-in user matches teacherId.
        List<Classroom> classrooms = classroomService.getClassroomsByTeacherId(teacherId);
        // It's okay to return an empty list if a teacher has no classrooms.
        // Returning 404 might be confusing if the teacher exists but has no classrooms.
        return ResponseEntity.ok(classrooms);
    }

    // Endpoint to get students in a specific classroom
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsInClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classroom not found with id: " + id));
        // Ensure that students are fetched (LAZY vs EAGER, @Transactional in service)
        return ResponseEntity.ok(classroom.getStudents());
    }

    // Endpoint for a student to enroll in a classroom using a join code
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

    // Endpoint to get the leaderboard for a classroom
    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable Long id) {
        try {
            List<LeaderboardEntryDto> leaderboard = classroomService.getLeaderboard(id);
            return ResponseEntity.ok(leaderboard);
        } catch (RuntimeException e) { // Catching potential "Classroom not found" from service
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // Endpoint to assign a game to a classroom
    @PostMapping("/{id}/assign-game")
    public ResponseEntity<ClassroomGame> assignGameToClassroom(
            @PathVariable Long id,
            @RequestParam Long gameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam boolean isPremade) {
        try {
            ClassroomGame classroomGame = classroomService.assignGameToClassroom(id, gameId, deadline, isPremade);
            return ResponseEntity.status(HttpStatus.CREATED).body(classroomGame); // 201 Created is suitable for new resource assignment
        } catch (RuntimeException e) {
            // Catch specific errors like ClassroomNotFound or GameNotFound from service
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // Endpoint to get all games assigned to a specific classroom
    @GetMapping("/{id}/games")
    public ResponseEntity<List<ClassroomGame>> getGamesForClassroom(@PathVariable Long id) {
        // Check if classroom exists before fetching games to return 404 if classroom itself is not found.
        if (!classroomService.getClassroomById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ClassroomGame> classroomGames = classroomService.getGamesForClassroom(id);
        return ResponseEntity.ok(classroomGames);
    }

    // Endpoint to add a student to a classroom by their ID (Teacher action)
    @PostMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<Classroom> addStudentToClassroomById(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        try {
            // Service layer should ideally handle fetching the student by ID
            Student studentToAdd = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
            Classroom updatedClassroom = classroomService.addStudentToClassroom(classroomId, studentToAdd);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // Endpoint to remove a student from a classroom by their ID (Teacher action)
    @DeleteMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<Classroom> removeStudentFromClassroomById(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        try {
            // Service layer should ideally handle fetching the student by ID
            Student studentToRemove = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
            Classroom updatedClassroom = classroomService.removeStudentFromClassroom(classroomId, studentToRemove);
            return ResponseEntity.ok(updatedClassroom);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // Endpoint to get general playground games (premade games)
    @GetMapping("/playground/games")
    public ResponseEntity<List<ClassroomGame>> getPlaygroundGames() {
        // This implies that ClassroomGame entities can exist without a specific classroom if they are "templates"
        // or that the service method filters premade games irrespective of classroom.
        List<ClassroomGame> playgroundGames = classroomService.getPlaygroundGames();
        return ResponseEntity.ok(playgroundGames);
    }

    // Endpoint to check a classroom by join code (e.g., for validation before actual enrollment)
    @GetMapping("/join/{joinCode}")
    public ResponseEntity<Classroom> getClassroomByJoinCode(@PathVariable String joinCode) {
        Classroom classroom = classroomService.findByJoinCode(joinCode);
        if (classroom != null) {
            // Consider returning a limited DTO for privacy if this is just for validation.
            return ResponseEntity.ok(classroom);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}