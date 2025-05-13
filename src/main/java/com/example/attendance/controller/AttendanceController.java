package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Course;
import com.example.attendance.model.User;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/attendances")
@Slf4j
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        log.info("Received GET request for all attendance records");
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance attendance) {
        log.info("Received POST request to create attendance record for student ID: " + attendance.getStudent().getId());
        return ResponseEntity.ok(attendanceService.createAttendance(attendance));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudent(@PathVariable("studentId") Long studentId) {
        log.info("Received GET request for attendance of student with ID: " + studentId);
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId));
    }

    @GetMapping("/course/{courseId}/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByCourseAndDate(@PathVariable("courseId") Long courseId, @PathVariable("date") String date) {
        log.info("Received GET request for attendance of course with ID: " + courseId + " on date: " + date);
        return ResponseEntity.ok(attendanceService.getAttendanceByCourseAndDate(courseId, date));
    }
}
