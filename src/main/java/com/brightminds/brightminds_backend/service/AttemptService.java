package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.StudentGameAttemptRequestDto;
import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.model.ClassroomGame; // Import ClassroomGame
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository; // Needed for ClassroomGame

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttemptService {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ClassroomGameRepository classroomGameRepository; // Autowire this

    @Transactional
    public Attempt recordAttempt(StudentGameAttemptRequestDto attemptDto) {
        // Validate and parse IDs
        Long studentIdLong;
        Long gameIdLong;
        Long assignedGameIdLong;
        try {
            studentIdLong = Long.parseLong(attemptDto.getStudentId());
            gameIdLong = Long.parseLong(attemptDto.getGameId());
            assignedGameIdLong = Long.parseLong(attemptDto.getAssignedGameId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format provided.");
        }

        Student student = studentRepository.findById(studentIdLong)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + attemptDto.getStudentId()));

        Game game = gameRepository.findById(gameIdLong)
                .orElseThrow(() -> new IllegalArgumentException("Game (Activity) not found with id: " + attemptDto.getGameId()));

        ClassroomGame classroomGame = classroomGameRepository.findById(assignedGameIdLong)
                .orElseThrow(() -> new IllegalArgumentException("ClassroomGame assignment not found with id: " + attemptDto.getAssignedGameId()));

        // Check deadline
        if (classroomGame.getDeadline() != null && LocalDateTime.now().isAfter(classroomGame.getDeadline())) {
            throw new RuntimeException("Deadline has passed for this game assignment.");
        }

        // Check max attempts
        Integer maxAttempts = classroomGame.getMaxAttempts();
        if (maxAttempts != null && maxAttempts > 0) { // maxAttempts > 0 means limited
            long existingAttemptsCount = attemptRepository.countByStudentIdAndClassroomGameId(studentIdLong, assignedGameIdLong);
            if (existingAttemptsCount >= maxAttempts) {
                throw new RuntimeException("Maximum number of attempts reached for this game assignment.");
            }
        }

        Attempt attempt = new Attempt();
        attempt.setStudent(student);
        attempt.setGame(game);
        attempt.setClassroomGame(classroomGame); // Linking attempt to the specific assignment

        attempt.setScore(attemptDto.getScore());

        if (attemptDto.getExpGained() != null) {
            attempt.setExpReward(attemptDto.getExpGained());
        } else {
            int gameMaxExp = game.getMaxExp() > 0 ? game.getMaxExp() : 50;
            int gameMaxScore = game.getMaxScore() > 0 ? game.getMaxScore() : 100;
            double scoreRatio = gameMaxScore > 0 ? (double) attemptDto.getScore() / gameMaxScore : 0;
            attempt.setExpReward(Math.max(0, (int) (scoreRatio * gameMaxExp)));
        }

        student.setExpAmount(student.getExpAmount() + attempt.getExpReward());
        studentRepository.save(student);

        if (attemptDto.getTimeTakenSeconds() != null) {
            attempt.setTimeTaken(attemptDto.getTimeTakenSeconds());
        } else {
            attempt.setTimeTaken(0);
        }

        attempt.setTimeStarted(LocalDateTime.now()); // Consider if client should send this
        attempt.setTimeFinished(LocalDateTime.now());

        return attemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public List<Attempt> getAttemptsByStudentAndAssignedGame(Long studentId, Long assignedGameId) {
        return attemptRepository.findByStudentIdAndClassroomGameId(studentId, assignedGameId);
    }

    @Transactional(readOnly = true)
    public List<Attempt> getAttemptsByStudentAndGame(Long studentId, Long gameActivityId) {
        return attemptRepository.findByStudentIdAndGameActivityId(studentId, gameActivityId);
    }
}