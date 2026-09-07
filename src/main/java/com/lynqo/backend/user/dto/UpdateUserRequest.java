package com.lynqo.backend.user.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Builder
@Data
public class UpdateUserRequest {
    private int userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String phoneNumber;
    private Date birthdate;
}
