package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.StudentGameAttemptRequestDto;
import com.brightminds.brightminds_backend.model.Attempt;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.model.Game;
import com.brightminds.brightminds_backend.model.ClassroomGame; // Import ClassroomGame
import com.brightminds.brightminds_backend.model.ClassroomScore;
import com.brightminds.brightminds_backend.repository.AttemptRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.repository.GameRepository;
import com.brightminds.brightminds_backend.repository.ClassroomGameRepository; // Needed for ClassroomGame
import com.brightminds.brightminds_backend.repository.ClassroomScoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private ClassroomScoreRepository classroomScoreRepository;

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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Deadline has passed for this game assignment.");
        }

        // Check max attempts
        Integer maxAttempts = classroomGame.getMaxAttempts();
        if (maxAttempts != null && maxAttempts > 0) { // maxAttempts > 0 means limited
            long existingAttemptsCount = attemptRepository.countByStudentIdAndClassroomGameId(studentIdLong, assignedGameIdLong);
            if (existingAttemptsCount >= maxAttempts) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Maximum number of attempts reached for this game assignment.");
            }
        }

        // Get existing attempts for this student and game
        List<Attempt> existingAttempts = attemptRepository.findByStudentIdAndClassroomGameId(studentIdLong, assignedGameIdLong);
        
        // Find the highest score among existing attempts
        int highestScore = existingAttempts.stream()
                .mapToInt(Attempt::getScore)
                .max()
                .orElse(0);

        // Validate that the new score doesn't exceed the game's max score
        if (attemptDto.getScore() > game.getMaxScore()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score cannot exceed the maximum score of " + game.getMaxScore());
        }

        // Create and save the new attempt
        Attempt attempt = new Attempt();
        attempt.setStudent(student);
        attempt.setGame(game);
        attempt.setClassroomGame(classroomGame);
        attempt.setScore(attemptDto.getScore());

        // Calculate EXP reward
        if (attemptDto.getExpGained() != null) {
            attempt.setExpReward(attemptDto.getExpGained());
        } else {
            int gameMaxExp = game.getMaxExp() > 0 ? game.getMaxExp() : 50;
            int gameMaxScore = game.getMaxScore() > 0 ? game.getMaxScore() : 100;
            double scoreRatio = gameMaxScore > 0 ? (double) attemptDto.getScore() / gameMaxScore : 0;
            attempt.setExpReward(Math.max(0, (int) (scoreRatio * gameMaxExp)));
        }

        // Only update classroom score and student EXP if this is a new highest score
        if (attemptDto.getScore() > highestScore) {
            // Get or create classroom score
            ClassroomScore classroomScore = classroomScoreRepository
                    .findByClassroomAndStudent(classroomGame.getClassroom(), student)
                    .orElseGet(() -> {
                        ClassroomScore newScore = new ClassroomScore();
                        newScore.setClassroom(classroomGame.getClassroom());
                        newScore.setStudent(student);
                        newScore.setTotalScore(0);
                        return newScore;
                    });

            // Update classroom score
            int scoreDifference = attemptDto.getScore() - highestScore;
            classroomScore.setTotalScore(classroomScore.getTotalScore() + scoreDifference);
            classroomScoreRepository.save(classroomScore);

            // Update student's EXP
            student.setExpAmount(student.getExpAmount() + attempt.getExpReward());
            studentRepository.save(student);
        } else {
            // For lower scores, set EXP reward to 0
            attempt.setExpReward(0);
        }

        // Set attempt timing
        if (attemptDto.getTimeTakenSeconds() != null) {
            attempt.setTimeTaken(attemptDto.getTimeTakenSeconds());
        } else {
            attempt.setTimeTaken(0);
        }
        attempt.setTimeStarted(LocalDateTime.now());
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