package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Course;
import com.example.attendance.model.User;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private static final Logger LOGGER = Logger.getLogger(AttendanceController.class.getName());

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Attendance> getAllAttendance() {
        LOGGER.info("Received GET request for all attendance records");
        return attendanceRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance attendance) {
        LOGGER.info("Received POST request to create attendance record for student ID: " + (attendance.getStudent() != null ? attendance.getStudent().getId() : "null"));
        // Проверяем студента
        if (attendance.getStudent() == null || attendance.getStudent().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> student = userRepository.findById(attendance.getStudent().getId());
        if (student.isEmpty() || student.get().getRole() != User.Role.STUDENT) {
            return ResponseEntity.badRequest().build();
        }
        // Проверяем курс
        if (attendance.getCourse() == null || attendance.getCourse().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Course> course = courseRepository.findById(attendance.getCourse().getId());
        if (course.isEmpty() || !course.get().getStudents().contains(student.get())) {
            return ResponseEntity.badRequest().build();
        }
        // Устанавливаем дату, если не указана
        if (attendance.getDate() == null) {
            attendance.setDate(LocalDate.now());
        }
        attendance.setStudent(student.get());
        attendance.setCourse(course.get());
        return ResponseEntity.ok(attendanceRepository.save(attendance));
    }

    @GetMapping("/student/{studentId}")
    public List<Attendance> getAttendanceByStudent(@PathVariable Long studentId) {
        LOGGER.info("Received GET request for attendance of student with ID: " + studentId);
        return attendanceRepository.findByStudentId(studentId);
    }

    @GetMapping("/course/{courseId}/{date}")
    public List<Attendance> getAttendanceByCourseAndDate(@PathVariable Long courseId, @PathVariable String date) {
        LOGGER.info("Received GET request for attendance of course with ID: " + courseId + " on date: " + date);
        LocalDate localDate = LocalDate.parse(date);
        return attendanceRepository.findByCourseIdAndDate(courseId, localDate);
    }


}
