package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.dto.CreateClassroomRequestDto;
import com.brightminds.brightminds_backend.dto.LeaderboardEntryDto;
import com.brightminds.brightminds_backend.dto.UpdateClassroomRequestDto;
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

    @Transactional
    public Classroom createClassroom(CreateClassroomRequestDto classroomDTO) {
        Teacher teacher = teacherRepository.findById(classroomDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + classroomDTO.getTeacherId()));

        Classroom classroom = new Classroom();
        classroom.setName(classroomDTO.getName());
        classroom.setDescription(classroomDTO.getDescription());
        classroom.setTeacher(teacher);
        classroom.generateJoinCode(); // Ensure this method is in your Classroom entity
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByTeacherId(Long teacherId) {
        // Ensure ClassroomRepository has findByTeacherId(Long teacherId)
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

        // 1. Delete associated ClassroomGames
        List<ClassroomGame> gamesInClassroom = classroomGameRepository.findByClassroomId(classroomId);
        if (gamesInClassroom != null && !gamesInClassroom.isEmpty()) {
            classroomGameRepository.deleteAll(gamesInClassroom);
        }

        // 2. Clear students from the classroom (handles the join table for @ManyToMany)
        if (classroom.getStudents() != null && !classroom.getStudents().isEmpty()) {
            // This clears the associations in the join table from the Classroom side.
            // The Student entities themselves are not deleted by this operation.
            classroom.getStudents().clear();
            // classroomRepository.save(classroom); // Save to persist the cleared collection if necessary before delete
            // Though, deleting the classroom itself should handle join table cleanup
            // if Classroom is the owner of the @ManyToMany.
        }

        // 3. Finally, delete the classroom itself
        classroomRepository.delete(classroom);
    }


    @Transactional
    public Classroom addStudentToClassroom(Long classroomId, Student student) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));
        // Ensure student is a managed entity if necessary, or that it's not already in the list
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
        // Ensure ClassroomRepository has findByJoinCode(String joinCode)
        return classroomRepository.findByJoinCode(joinCode);
    }

    @Transactional
    public Classroom addStudentByJoinCode(String joinCode, Student student) {
        Classroom classroom = findByJoinCode(joinCode); // Use the method to find by join code
        if (classroom == null) {
            throw new RuntimeException("Classroom not found for join code: " + joinCode);
        }
        if (student != null && !classroom.getStudents().contains(student)) {
            classroom.getStudents().add(student);
            // classroomRepository.save(classroom); // Changes to managed entity are often automatically persisted at transaction commit.
        }
        return classroom;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));

        List<Student> students = classroom.getStudents();

        if (students == null || students.isEmpty()) {
            return Collections.emptyList();
        }

        return students.stream()
                .filter(s -> s != null && s.getId() != null && s.getFirstName() != null && s.getLastName() != null) // Ensure student and key fields are not null
                .sorted(Comparator.comparingInt(Student::getExpAmount).reversed())
                .map(student -> new LeaderboardEntryDto(
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getExpAmount(),
                        student.getAvatarImage()
                ))
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

    @Transactional(readOnly = true)
    public List<ClassroomGame> getGamesForClassroom(Long classroomId) {
        // Ensure ClassroomGameRepository has findByClassroomId(Long classroomId)
        return classroomGameRepository.findByClassroomId(classroomId);
    }

    @Transactional(readOnly = true)
    public List<ClassroomGame> getPlaygroundGames() {
        // Ensure ClassroomGameRepository has findByGameIsPremadeTrue()
        return classroomGameRepository.findByGameIsPremadeTrue();
    }
}