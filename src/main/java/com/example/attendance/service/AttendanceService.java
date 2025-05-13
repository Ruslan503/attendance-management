package com.example.attendance.service;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Course;
import com.example.attendance.model.User;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance createAttendance(Attendance attendance) {
        User student = userRepository.findById(attendance.getStudent().getId())
                .filter(u -> u.getRole() == User.Role.STUDENT)
                .orElseThrow(() -> new IllegalArgumentException("Student not found or invalid role"));
        Course course = courseRepository.findById(attendance.getCourse().getId())
                .filter(c -> c.getStudents().contains(student))
                .orElseThrow(() -> new IllegalArgumentException("Course not found or student not enrolled"));
        if (attendance.getDate() == null) {
            attendance.setDate(LocalDate.now());
        }
        attendance.setStudent(student);
        attendance.setCourse(course);
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> getAttendanceByCourseAndDate(Long courseId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return attendanceRepository.findByCourseIdAndDate(courseId, localDate);
    }
}
