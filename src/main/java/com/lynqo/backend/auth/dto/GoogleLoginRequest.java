package com.lynqo.backend.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginRequest {
    private String username;
    private String email;
    private String accessToken;
}
