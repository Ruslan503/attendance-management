package com.example.attendance.controller;

import com.example.attendance.model.User;
import com.example.attendance.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List getAllUsers() {
        LOGGER.info("Received GET request for all users");
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        LOGGER.info("Received POST request to create user: " + user.getName());
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        LOGGER.info("Received GET request for user with ID: " + id);
        Optional user = userRepository.findById(id);
        return (ResponseEntity) user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        LOGGER.info("Received PUT request to update user with ID: " + id);
        Optional userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = (User) userOptional.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            return ResponseEntity.ok(userRepository.save(user));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        LOGGER.info("Received DELETE request for user with ID: " + id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
