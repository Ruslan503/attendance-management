package com.example.attendance.controller;

import com.example.attendance.model.Course;
import com.example.attendance.model.User;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger LOGGER = Logger.getLogger(CourseController.class.getName());

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Course> getAllCourses() {
        LOGGER.info("Received GET request for all courses");
        return courseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        LOGGER.info("Received POST request to create course: " + course.getName());
        if (course.getFaculty() != null && course.getFaculty().getId() != null) {
            Optional<User> faculty = userRepository.findById(course.getFaculty().getId());
            if (faculty.isPresent() && faculty.get().getRole() == User.Role.FACULTY) {
                course.setFaculty(faculty.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        if (course.getStudents() != null) {
            List<User> validStudents = course.getStudents().stream()
                    .map(student -> userRepository.findById(student.getId()).orElse(null))
                    .filter(student -> student != null && student.getRole() == User.Role.STUDENT)
                    .toList();
            course.setStudents(validStudents);
        }
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        LOGGER.info("Received GET request for course with ID: " + id);
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course courseDetails) {
        LOGGER.info("Received PUT request to update course with ID: " + id);
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setName(courseDetails.getName());
            if (courseDetails.getFaculty() != null && courseDetails.getFaculty().getId() != null) {
                Optional<User> faculty = userRepository.findById(courseDetails.getFaculty().getId());
                if (faculty.isPresent() && faculty.get().getRole() == User.Role.FACULTY) {
                    course.setFaculty(faculty.get());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            if (courseDetails.getStudents() != null) {
                List<User> validStudents = courseDetails.getStudents().stream()
                        .map(student -> userRepository.findById(student.getId()).orElse(null))
                        .filter(student -> student != null && student.getRole() == User.Role.STUDENT)
                        .toList();
                course.setStudents(validStudents);
            }
            return ResponseEntity.ok(courseRepository.save(course));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        LOGGER.info("Received DELETE request for course with ID: " + id);
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
