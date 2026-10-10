package com.student360.service;

import com.student360.dto.CreateUserRequest;
import com.student360.dto.ResetPasswordRequest;
import com.student360.dto.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> listAll();
    List<UserResponse> listByRole(String role);
    UserResponse getById(Long id);
    UserResponse create(CreateUserRequest request);
    void resetPassword(Long id, ResetPasswordRequest request);
    void disableUser(Long id);
    void enableUser(Long id);
}
