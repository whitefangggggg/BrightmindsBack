package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.Classroom;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;

    public Classroom createClassroom(Classroom classroom) {
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
}