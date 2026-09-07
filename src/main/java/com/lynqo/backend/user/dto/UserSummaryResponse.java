package com.lynqo.backend.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {
    private int id;
    private String username;
    private String avatarUrl;
    private boolean active;
}
