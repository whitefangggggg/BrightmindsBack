package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;

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
}