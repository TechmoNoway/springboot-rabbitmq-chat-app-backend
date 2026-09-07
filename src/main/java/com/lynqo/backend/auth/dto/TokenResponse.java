package com.lynqo.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String userId;
    private String accessToken;
    private String refreshToken;

    @JsonIgnore
    private String accessTokenId;

    @JsonIgnore
    private String refreshTokenId;
}
