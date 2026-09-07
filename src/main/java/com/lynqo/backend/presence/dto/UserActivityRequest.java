package com.lynqo.backend.presence.dto;

import lombok.Data;

@Data
public class UserActivityRequest {
    private int userId;
    private String status;
}
