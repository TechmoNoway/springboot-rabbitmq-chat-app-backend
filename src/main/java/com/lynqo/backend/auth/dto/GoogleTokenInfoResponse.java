package com.lynqo.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleTokenInfoResponse {
    private String aud;
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("expires_in")
    private Long expiresIn;
}
