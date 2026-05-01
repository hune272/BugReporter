package com.bug_reporter.backend.service;

import com.bug_reporter.backend.repository.UserRepository;
import com.bug_reporter.backend.repository.VoteRepository;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.VoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bug_reporter.backend.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, VoteRepository voteRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //Read-only methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsers(String search, Integer limit) {
        int safeLimit = limit == null ? 25 : Math.max(1, Math.min(limit, 100));
        if (search == null || search.isBlank()) {
            return userRepository.findAllByOrderByUsernameAsc(PageRequest.of(0, safeLimit));
        }
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByUsernameAsc(
                search,
                search,
                PageRequest.of(0, safeLimit)
        );
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    //Create -- Update -- Delete
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists" + user.getEmail());
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists" + user.getUsername());
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public double getUserScore(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return calculateUserScores().getOrDefault(userId, 0.0);
    }

    public Map<Long, Double> getUserScores() {
        return calculateUserScores();
    }

    private Map<Long, Double> calculateUserScores() {
        Map<Long, Double> scores = new HashMap<>();

        for (Vote vote : voteRepository.findAll()) {
            boolean upvote = vote.getType() == VoteType.UPVOTE;

            if (vote.getBug() != null && vote.getBug().getAuthor() != null) {
                Long authorId = vote.getBug().getAuthor().getId();
                double delta = upvote ? 2.5 : -1.5;
                scores.merge(authorId, delta, Double::sum);
            }

            if (vote.getComment() != null && vote.getComment().getAuthor() != null) {
                Long authorId = vote.getComment().getAuthor().getId();
                double delta = upvote ? 5.0 : -2.5;
                scores.merge(authorId, delta, Double::sum);

                if (!upvote && vote.getUser() != null && !vote.getUser().getId().equals(authorId)) {
                    scores.merge(vote.getUser().getId(), -1.5, Double::sum);
                }
            }
        }

        return scores;
    }
}