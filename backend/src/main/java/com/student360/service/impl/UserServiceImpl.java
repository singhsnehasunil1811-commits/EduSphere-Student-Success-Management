package com.student360.service.impl;

import com.student360.dto.CreateUserRequest;
import com.student360.dto.ResetPasswordRequest;
import com.student360.dto.UserResponse;
import com.student360.entity.User;
import com.student360.exception.DuplicateResourceException;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.UserRepository;
import com.student360.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> listAll() {
        return userRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<UserResponse> listByRole(String role) {
        return userRepo.findByRole(role.toUpperCase()).stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        userRepo.findByEmail(request.email().trim().toLowerCase())
            .ifPresent(u -> {
                throw new DuplicateResourceException("User with email '" + request.email() + "' already exists");
            });

        String role = request.role().toUpperCase().trim();
        if (!role.equals("ADMIN") && !role.equals("TEACHER") && !role.equals("STUDENT")) {
            throw new IllegalArgumentException("Role must be ADMIN, TEACHER, or STUDENT");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setFullName(request.fullName().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setEnabled(true);
        return toResponse(userRepo.save(user));
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = findById(id);
        user.setEnabled(false);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        User user = findById(id);
        user.setEnabled(true);
        userRepo.save(user);
    }

    private User findById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
            u.getId(),
            u.getEmail(),
            u.getFullName(),
            u.getRole(),
            u.isEnabled()
        );
    }
}
