package com.lynqo.backend.auth.service;

import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService {

    TokenResponse register(SignupRequest signupDTO);

    TokenResponse login(LoginRequest loginDTO);

    TokenResponse loginWithGoogle(GoogleLoginRequest loginWithGoogleRequest) throws GeneralSecurityException, IOException;

    TokenResponse refresh(String refreshToken);

    void logout(int userId);
}
