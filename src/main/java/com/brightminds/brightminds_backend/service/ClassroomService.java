package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.LeaderboardEntryDto;
import com.brightminds.brightminds_backend.dto.UpdateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.UpdateAssignedGameRequestDto; // Make sure this DTO exists
import com.brightminds.brightminds_backend.exception.ClassroomAlreadyJoinedException;
import com.brightminds.brightminds_backend.exception.ResourceNotFoundException;
import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.model.ClassroomGame;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.model.ClassroomScore;
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.ClassroomScoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

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
        if (classroomRepository.findByName(classroomDTO.getName()) != null) {
            throw new RuntimeException("A classroom with the name '" + classroomDTO.getName() + "' already exists. Please choose a different name.");
        }
        Teacher teacher = teacherRepository.findById(classroomDTO.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + classroomDTO.getTeacherId()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId + " for update."));
        if (updateDto.getName() != null && !updateDto.getName().isEmpty() && !updateDto.getName().equals(classroom.getName())) {
            Classroom existingWithName = classroomRepository.findByName(updateDto.getName());
            if (existingWithName != null && !existingWithName.getId().equals(classroomId)) {
                throw new RuntimeException("Another classroom with the name '" + updateDto.getName() + "' already exists.");
            }
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
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId + " for deletion."));
        List<ClassroomGame> gamesInClassroom = classroomGameRepository.findByClassroomId(classroomId);
        if (gamesInClassroom != null && !gamesInClassroom.isEmpty()) {
            for (ClassroomGame cg : gamesInClassroom) {
                List<Attempt> attemptsForCg = attemptRepository.findByClassroomGame(cg); //
                if (attemptsForCg != null && !attemptsForCg.isEmpty()) {
                    attemptRepository.deleteAll(attemptsForCg);
                }
            }
            classroomGameRepository.deleteAll(gamesInClassroom);
        }
        List<ClassroomScore> scoresInClassroom = classroomScoreRepository.findByClassroomOrderByTotalScoreDesc(classroom); //
        if (scoresInClassroom != null && !scoresInClassroom.isEmpty()) {
            classroomScoreRepository.deleteAll(scoresInClassroom);
        }
        if (classroom.getStudents() != null && !classroom.getStudents().isEmpty()) {
            classroom.getStudents().clear();
        }
        classroomRepository.delete(classroom);
    }

    @Transactional
    public Classroom addStudentToClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        if (student != null && !classroom.getStudents().contains(student)) {
            classroom.getStudents().add(student);
        }
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom removeStudentFromClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        if (student != null) {
            classroom.getStudents().remove(student);
            classroomScoreRepository.findByClassroomAndStudent(classroom, student).ifPresent(classroomScoreRepository::delete); //
        }
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public Classroom findByJoinCode(String joinCode) {
        Classroom classroom = classroomRepository.findByJoinCode(joinCode); //
        if (classroom == null) {
            throw new ResourceNotFoundException("Classroom not found for join code: " + joinCode);
        }
        return classroom;
    }

    @Transactional
    public Classroom addStudentByJoinCode(String joinCode, Student student) {
        Classroom classroom = findByJoinCode(joinCode);
        if (student != null) {
            if (classroom.getStudents().contains(student)) {
                throw new ClassroomAlreadyJoinedException("You have already joined this classroom: " + classroom.getName());
            }
            classroom.getStudents().add(student);
            if (!classroomScoreRepository.findByClassroomAndStudent(classroom, student).isPresent()) { //
                ClassroomScore score = new ClassroomScore();
                score.setClassroom(classroom);
                score.setStudent(student);
                score.setTotalScore(0);
                classroomScoreRepository.save(score);
            }
        }
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        List<ClassroomScore> scores = classroomScoreRepository.findByClassroomOrderByTotalScoreDesc(classroom); //
        if (scores.isEmpty()) {
            return Collections.emptyList();
        }
        return scores.stream()
                .map(score -> new LeaderboardEntryDto(
                        score.getStudent().getId(),
                        score.getStudent().getFirstName(),
                        score.getStudent().getLastName(),
                        score.getTotalScore(),
                        score.getStudent().getAvatarImage()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassroomGame assignGameToClassroom(Long classroomId, Long gameId, LocalDateTime deadline, boolean isPremade, Integer maxAttempts) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
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
        if (!classroomRepository.existsById(classroomId)) {
            throw new ResourceNotFoundException("Classroom not found with id: " + classroomId);
        }
        return classroomGameRepository.findByClassroomId(classroomId); //
    }

    @Transactional(readOnly = true)
    public List<ClassroomGame> getPlaygroundGames() {
        return classroomGameRepository.findByGameIsPremadeTrue(); //
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getGameLeaderboard(Long classroomId, Long gameActivityId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));

        List<ClassroomGame> relevantAssignments = classroomGameRepository.findByClassroomIdAndGameActivityId(classroomId, gameActivityId); //

        if (relevantAssignments.isEmpty()) {
            return Collections.emptyList();
        }

        List<LeaderboardEntryDto> leaderboardEntries = new ArrayList<>();

        for (Student student : classroom.getStudents()) {
            int maxScoreForStudent = 0;
            boolean attempted = false;
            for (ClassroomGame assignment : relevantAssignments) {
                // Corrected to use the ID-based method from AttemptRepository
                List<Attempt> attempts = attemptRepository.findByStudentIdAndClassroomGameId(student.getId(), assignment.getId()); //
                for (Attempt attempt : attempts) {
                    attempted = true;
                    if (attempt.getScore() > maxScoreForStudent) {
                        maxScoreForStudent = attempt.getScore();
                    }
                }
            }
            if (attempted) {
                leaderboardEntries.add(new LeaderboardEntryDto(
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        maxScoreForStudent,
                        student.getAvatarImage()));
            }
        }
        // Corrected the method reference here based on your LeaderboardEntryDto
        leaderboardEntries.sort(Comparator.comparingInt(LeaderboardEntryDto::getExpAmount).reversed()); //
        return leaderboardEntries;
    }

    @Transactional
    public ClassroomGame updateAssignedGameDetails(Long classroomId, Long assignedGameId, UpdateAssignedGameRequestDto updateRequest) {
        if (!classroomRepository.existsById(classroomId)) {
            throw new ResourceNotFoundException("Classroom not found with id: " + classroomId);
        }
        ClassroomGame assignedGame = classroomGameRepository.findById(assignedGameId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned game (ClassroomGame) not found with id: " + assignedGameId));

        if (!assignedGame.getClassroom().getId().equals(classroomId)) {
            throw new SecurityException("Assigned game does not belong to the specified classroom.");
        }

        if (updateRequest.getDeadline() != null) {
            assignedGame.setDeadline(updateRequest.getDeadline());
        }
        if (updateRequest.getMaxAttempts() == null || updateRequest.getMaxAttempts() > 0) {
            assignedGame.setMaxAttempts(updateRequest.getMaxAttempts());
        } else if (updateRequest.getMaxAttempts() <=0) { // Allow 0 for unlimited, or set to null
            throw new IllegalArgumentException("Max attempts must be null (for unlimited) or a positive integer.");
        }
        return classroomGameRepository.save(assignedGame);
    }

    @Transactional
    public void deleteAssignedGame(Long classroomId, Long assignedGameId) {
        if (!classroomRepository.existsById(classroomId)) {
            throw new ResourceNotFoundException("Classroom not found with id: " + classroomId);
        }
        ClassroomGame assignedGame = classroomGameRepository.findById(assignedGameId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned game (ClassroomGame) not found with id: " + assignedGameId));

        if (!assignedGame.getClassroom().getId().equals(classroomId)) {
            throw new SecurityException("Assigned game does not belong to the specified classroom. Cannot delete.");
        }

        List<Attempt> attemptsToDelete = attemptRepository.findByClassroomGame(assignedGame); //
        if (attemptsToDelete != null && !attemptsToDelete.isEmpty()) {
            attemptRepository.deleteAll(attemptsToDelete);
        }
        classroomGameRepository.delete(assignedGame);
    }
}