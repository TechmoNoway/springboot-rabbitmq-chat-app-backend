package com.lynqo.backend.user.dto;

import lombok.Builder;

@Builder
public class ChangePasswordRequest {
    int userId;
    String newPassword;
    String confirmPassword;
}
