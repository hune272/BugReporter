package com.bug_reporter.backend.service;

import com.bug_reporter.backend.dto.request.UserUpdateRequest;
import com.bug_reporter.backend.dto.response.UserResponse;
import com.bug_reporter.backend.dto.mapper.UserMapper;
import com.bug_reporter.backend.repository.UserRepository;
import com.bug_reporter.backend.repository.VoteRepository;
import com.bug_reporter.backend.model.Vote;
import com.bug_reporter.backend.model.enums.UserRole;
import com.bug_reporter.backend.model.enums.VoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public List<UserResponse> getAllUserResponses() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public List<UserResponse> getUserResponses(String search, Integer limit) {
        int safeLimit = limit == null ? 25 : Math.max(1, Math.min(limit, 100));
        List<User> users;
        if (search == null || search.isBlank()) {
            users = userRepository.findAllByOrderByUsernameAsc(PageRequest.of(0, safeLimit));
        } else {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByUsernameAsc(
                    search,
                    search,
                    PageRequest.of(0, safeLimit)
            );
        }
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public Optional<UserResponse> getUserResponseById(Long id) {
        return userRepository.findById(id).map(UserMapper::toResponse);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request, Long requesterId) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        User requester = getUser(requesterId);
        boolean moderator = requester.getRole() == UserRole.MODERATOR;

        if (!moderator && !requester.getId().equals(existingUser.getId())) {
            throw new SecurityException("You are not allowed to update this user");
        }

        if (!moderator && request.role() != existingUser.getRole()) {
            throw new SecurityException("Only moderators can change user roles");
        }

        existingUser.setUsername(request.username());
        existingUser.setEmail(request.email());
        existingUser.setRole(request.role());
        if (request.password() != null && !request.password().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }
        return UserMapper.toResponse(userRepository.save(existingUser));
    }

    public void deleteUser(Long id, Long requesterId) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        User requester = getUser(requesterId);

        if (requester.getRole() != UserRole.MODERATOR && !requester.getId().equals(existingUser.getId())) {
            throw new SecurityException("You are not allowed to delete this user");
        }
        userRepository.delete(existingUser);
    }

    public UserResponse banUser(Long id, Long requesterId) {
        User requester = getModerator(requesterId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (requester.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Moderators cannot ban themselves");
        }

        user.setBanned(true);
        notifyUserBanned(user);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public UserResponse unbanUser(Long id, Long requesterId) {
        getModerator(requesterId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setBanned(false);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public double getUserScore(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return calculateUserScores().getOrDefault(userId, 0.0);
    }

    @Transactional(readOnly = true)
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

    private User getModerator(Long requesterId) {
        User requester = getUser(requesterId);
        if (requester.getRole() != UserRole.MODERATOR) {
            throw new SecurityException("Only moderators can perform this action");
        }
        return requester;
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new SecurityException("Not authenticated");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private void notifyUserBanned(User user) {
        System.out.println("Email notification: user " + user.getEmail() + " was banned.");
        System.out.println("SMS notification: user " + user.getUsername() + " was banned.");
    }
}
