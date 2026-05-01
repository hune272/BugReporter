package com.bug_reporter.backend.controller;

import com.bug_reporter.backend.model.User;
import com.bug_reporter.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit) {
        if (search != null || limit != null) {
            return ResponseEntity.ok(userService.getUsers(search, limit));
        }
        return ResponseEntity.ok(userService.getAllUsers());
        //HTTP 200 OK
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return getAllUsers(null, null);
    }

    @GetMapping("/scores")
    public ResponseEntity<Map<Long, Double>> getUserScores() {
        return ResponseEntity.ok(userService.getUserScores());
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<?> getUserScore(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("userId", id, "score", userService.getUserScore(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        //200 OK or 404 Not Found
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try{
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        }
        catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try{
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

}