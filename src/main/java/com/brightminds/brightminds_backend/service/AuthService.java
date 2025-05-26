package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.User;
import com.brightminds.brightminds_backend.model.Teacher;
import com.brightminds.brightminds_backend.model.Student;
import com.brightminds.brightminds_backend.repository.UserRepository;
import com.brightminds.brightminds_backend.repository.TeacherRepository;
import com.brightminds.brightminds_backend.repository.StudentRepository;
import com.brightminds.brightminds_backend.util.JwtUtil;
import com.brightminds.brightminds_backend.dto.RegisterRequestDto;
import com.brightminds.brightminds_backend.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${teacher.code}")
    private String teacherCode;

    public User register(RegisterRequestDto registerRequest) {
        if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
            throw new AuthException("First name is required");
        }
        if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
            throw new AuthException("Last name is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            throw new AuthException("Password must be at least 6 characters long");
        }
        if (registerRequest.getRole() == null || !(registerRequest.getRole().equalsIgnoreCase("TEACHER") || registerRequest.getRole().equalsIgnoreCase("STUDENT"))) {
            throw new AuthException("Role must be either 'TEACHER' or 'STUDENT'");
        }
        if (registerRequest.getRole().equalsIgnoreCase("TEACHER")) {
            if (registerRequest.getTeacherCode() == null || !registerRequest.getTeacherCode().equals(teacherCode)) {
                throw new AuthException("Invalid or missing teacher code");
            }
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new AuthException("User with email " + registerRequest.getEmail() + " already exists");
        }

        User user;
        String roleFromRequest = registerRequest.getRole().toUpperCase();

        if (roleFromRequest.equals("TEACHER")) {
            Teacher teacher = new Teacher();
            teacher.setEmail(registerRequest.getEmail());
            teacher.setPassword(registerRequest.getPassword());
            teacher.setFirstName(registerRequest.getFirstName());
            teacher.setLastName(registerRequest.getLastName());
            teacher.setRole(roleFromRequest);
            user = teacherRepository.save(teacher);
        } else {
            Student student = new Student();
            student.setEmail(registerRequest.getEmail());
            student.setPassword(registerRequest.getPassword());
            student.setFirstName(registerRequest.getFirstName());
            student.setLastName(registerRequest.getLastName());
            student.setRole(roleFromRequest);
            user = studentRepository.save(student);
        }
        return user;
    }

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found with email: " + email));
        if (!user.getPassword().equals(password)) {
            throw new AuthException("Invalid password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user);
    }

    public static class LoginResponse {
        private String accessToken;
        private User user;

        public LoginResponse(String accessToken, User user) {
            this.accessToken = accessToken;
            this.user = user;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public User getUser() {
            return user;
        }
    }
}