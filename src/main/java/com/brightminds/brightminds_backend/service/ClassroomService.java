package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private TeacherRepository teacherRepository; // Make sure this is autowired

    @Autowired
    private ClassroomGameRepository classroomGameRepository;

    @Autowired
    private GameRepository gameRepository;

    // Corrected createClassroom method using CreateClassroomRequestDTO
    @Transactional
    public Classroom createClassroom(CreateClassroomRequestDto classroomDTO) {
        Teacher teacher = teacherRepository.findById(classroomDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + classroomDTO.getTeacherId()));

        Classroom classroom = new Classroom();
        classroom.setName(classroomDTO.getName());
        classroom.setDescription(classroomDTO.getDescription());
        classroom.setTeacher(teacher); // Set the fetched Teacher entity
        classroom.generateJoinCode();
        return classroomRepository.save(classroom);
    }

    // Method to get classrooms by teacherId (added from previous suggestions)
    public List<Classroom> getClassroomsByTeacherId(Long teacherId) {
        // This assumes your ClassroomRepository has a method: List<Classroom> findByTeacherId(Long teacherId);
        // If not, you'll need to add it to ClassroomRepository.java
        return classroomRepository.findByTeacherId(teacherId);
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Transactional
    public Classroom addStudentToClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        // Ensure student is managed or fetched if only ID is passed
        if (!classroom.getStudents().contains(student)) { // Avoid duplicates if student already exists by reference
            classroom.getStudents().add(student);
        }
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom removeStudentFromClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        // Ensure student is managed or fetched if only ID is passed for removal
        classroom.getStudents().remove(student);
        return classroomRepository.save(classroom);
    }

    public Classroom findByJoinCode(String joinCode) {
        return classroomRepository.findByJoinCode(joinCode);
    }

    @Transactional
    public Classroom addStudentByJoinCode(String joinCode, Student student) {
        Classroom classroom = classroomRepository.findByJoinCode(joinCode);
        if (classroom == null) {
            throw new RuntimeException("Classroom not found for join code: " + joinCode);
        }
        if (!classroom.getStudents().contains(student)) {
            classroom.getStudents().add(student);
            // classroomRepository.save(classroom); // Save is handled by @Transactional if changes are made to a managed entity
        }
        return classroom; // Return the managed classroom entity
    }

    public List<Student> getLeaderboard(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        return classroom.getStudents().stream()
                .sorted(Comparator.comparingInt(Student::getExpAmount).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassroomGame assignGameToClassroom(Long classroomId, Long gameId, LocalDateTime deadline, boolean isPremade) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        ClassroomGame classroomGame = new ClassroomGame();
        classroomGame.setClassroom(classroom);
        classroomGame.setGame(game);
        classroomGame.setDeadline(deadline);
        classroomGame.setPremade(isPremade);
        return classroomGameRepository.save(classroomGame);
    }

    public List<ClassroomGame> getGamesForClassroom(Long classroomId) {
        return classroomGameRepository.findByClassroomId(classroomId);
    }

    public List<ClassroomGame> getPlaygroundGames() {
        return classroomGameRepository.findByGameIsPremadeTrue();
    }
}