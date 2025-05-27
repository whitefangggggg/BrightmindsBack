package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ClassroomGameRepository classroomGameRepository;

    @Autowired
    private GameRepository gameRepository;

    public Classroom createClassroom(Classroom classroom) {
        classroom.generateJoinCode();
        return classroomRepository.save(classroom);
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public List<Classroom> getClassroomsByTeacherId(Long teacherId) {
        // This assumes your ClassroomRepository has a method like findByTeacherId
        return classroomRepository.findByTeacherId(teacherId);
    }

    public Classroom addStudentToClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow();
        classroom.getStudents().add(student);
        return classroomRepository.save(classroom);
    }

    public Classroom removeStudentFromClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow();
        classroom.getStudents().remove(student);
        return classroomRepository.save(classroom);
    }

    public Classroom findByJoinCode(String joinCode) {
        return classroomRepository.findByJoinCode(joinCode);
    }

    public Classroom addStudentByJoinCode(String joinCode, Student student) {
        Classroom classroom = classroomRepository.findByJoinCode(joinCode);
        if (classroom == null) throw new RuntimeException("Classroom not found for join code: " + joinCode);
        if (!classroom.getStudents().contains(student)) {
            classroom.getStudents().add(student);
            classroomRepository.save(classroom);
        }
        return classroom;
    }

    public List<Student> getLeaderboard(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow();
        return classroom.getStudents().stream()
                .sorted(Comparator.comparingInt(Student::getExpAmount).reversed())
                .collect(Collectors.toList());
    }

    public ClassroomGame assignGameToClassroom(Long classroomId, Long gameId, LocalDateTime deadline, boolean isPremade) {
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow();
        Game game = gameRepository.findById(gameId).orElseThrow();
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