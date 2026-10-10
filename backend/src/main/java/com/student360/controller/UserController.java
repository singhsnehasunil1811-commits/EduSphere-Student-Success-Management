package com.student360.controller;

import com.student360.dto.CreateUserRequest;
import com.student360.dto.ResetPasswordRequest;
import com.student360.dto.UserResponse;
import com.student360.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list(@RequestParam(required = false) String role) {
        if (role != null && !role.isBlank()) {
            return userService.listByRole(role);
        }
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
        @PathVariable Long id,
        @Valid @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Map<String, String>> disable(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Map<String, String>> enable(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok(Map.of("message", "User enabled successfully"));
    }
}
