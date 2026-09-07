package com.lynqo.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleLoginResponse {
    private int userId;
}
