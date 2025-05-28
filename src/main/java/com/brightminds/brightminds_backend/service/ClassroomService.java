package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.LeaderboardEntryDto;
import com.brightminds.brightminds_backend.dto.UpdateClassroomRequestDto;
import com.brightminds.brightminds_backend.exception.ClassroomAlreadyJoinedException;
import com.brightminds.brightminds_backend.model.Attempt; // Import Attempt
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.model.ClassroomScore;
import com.brightminds.brightminds_backend.repository.AttemptRepository; // Import AttemptRepository
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.ClassroomScoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassroomGameRepository classroomGameRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private ClassroomScoreRepository classroomScoreRepository;

    @Transactional
    public Classroom createClassroom(CreateClassroomRequestDto classroomDTO) {
        Teacher teacher = teacherRepository.findById(classroomDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + classroomDTO.getTeacherId()));

        Classroom classroom = new Classroom();
        classroom.setName(classroomDTO.getName());
        classroom.setDescription(classroomDTO.getDescription());
        classroom.setTeacher(teacher);
        classroom.generateJoinCode();
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByTeacherId(Long teacherId) {
        return classroomRepository.findByTeacherId(teacherId);
    }

    @Transactional(readOnly = true)
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Transactional
    public Classroom updateClassroom(Long classroomId, UpdateClassroomRequestDto updateDto) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId + " for update."));

        if (updateDto.getName() != null && !updateDto.getName().isEmpty() && !updateDto.getName().equals(classroom.getName())) {
            classroom.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            classroom.setDescription(updateDto.getDescription().isEmpty() ? null : updateDto.getDescription());
        }
        return classroomRepository.save(classroom);
    }

    @Transactional
    public void deleteClassroom(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId + " for deletion."));

        // 1. Find all ClassroomGames associated with this classroom
        List<ClassroomGame> gamesInClassroom = classroomGameRepository.findByClassroomId(classroomId);
        if (gamesInClassroom != null && !gamesInClassroom.isEmpty()) {
            // For each ClassroomGame, delete associated Attempts first
            for (ClassroomGame cg : gamesInClassroom) {
                // Ensure AttemptRepository has findByClassroomGame(ClassroomGame classroomGame)
                List<Attempt> attemptsForCg = attemptRepository.findByClassroomGame(cg);
                if (attemptsForCg != null && !attemptsForCg.isEmpty()) {
                    attemptRepository.deleteAll(attemptsForCg);
                }
            }
            // Now, delete the ClassroomGames themselves
            classroomGameRepository.deleteAll(gamesInClassroom);
        }

        // 2. Clear students from the classroom and save to update the join table
        if (classroom.getStudents() != null && !classroom.getStudents().isEmpty()) {
            classroom.getStudents().clear();
            classroomRepository.save(classroom);
        }

        // 3. Finally, delete the classroom itself
        classroomRepository.delete(classroom);
    }


    @Transactional
    public Classroom addStudentToClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        if (student != null && !classroom.getStudents().contains(student)) {
            classroom.getStudents().add(student);
        }
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom removeStudentFromClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        if (student != null) {
            classroom.getStudents().remove(student);
        }
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public Classroom findByJoinCode(String joinCode) {
        return classroomRepository.findByJoinCode(joinCode);
    }

    @Transactional
    public Classroom addStudentByJoinCode(String joinCode, Student student) {
        Classroom classroom = findByJoinCode(joinCode);
        if (classroom == null) {
            throw new RuntimeException("Classroom not found for join code: " + joinCode);
        }
        if (student != null) {
            if (classroom.getStudents().contains(student)) {
                throw new ClassroomAlreadyJoinedException("You have already joined this classroom");
            }
            classroom.getStudents().add(student);
            
            // Initialize classroom score for the new student
            ClassroomScore score = new ClassroomScore();
            score.setClassroom(classroom);
            score.setStudent(student);
            score.setTotalScore(0);
            classroomScoreRepository.save(score);
        }
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));

        List<ClassroomScore> scores = classroomScoreRepository.findByClassroomOrderByTotalScoreDesc(classroom);

        if (scores.isEmpty()) {
            return Collections.emptyList();
        }

        return scores.stream()
                .map(score -> {
                    Student student = score.getStudent();
                    return new LeaderboardEntryDto(
                            student.getId(),
                            student.getFirstName(),
                            student.getLastName(),
                            score.getTotalScore(), // Use classroom-specific score instead of global exp
                            student.getAvatarImage()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassroomGame assignGameToClassroom(Long classroomId, Long gameId, LocalDateTime deadline, boolean isPremade, Integer maxAttempts) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        ClassroomGame classroomGame = new ClassroomGame();
        classroomGame.setClassroom(classroom);
        classroomGame.setGame(game);
        classroomGame.setDeadline(deadline);
        classroomGame.setPremade(isPremade);
        classroomGame.setMaxAttempts(maxAttempts);
        return classroomGameRepository.save(classroomGame);
    }

    @Transactional(readOnly = true)
    public List<ClassroomGame> getGamesForClassroom(Long classroomId) {
        return classroomGameRepository.findByClassroomId(classroomId);
    }

    @Transactional(readOnly = true)
    public List<ClassroomGame> getPlaygroundGames() {
        return classroomGameRepository.findByGameIsPremadeTrue();
    }
}