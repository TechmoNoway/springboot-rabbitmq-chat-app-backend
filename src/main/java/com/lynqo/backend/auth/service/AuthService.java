package com.lynqo.backend.auth.service;

import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.dto.GoogleLoginResponse;
import com.lynqo.backend.user.domain.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService {

    void saveUserToken(User user, String jwtToken);

    TokenResponse register(SignupRequest signupDTO);

    TokenResponse login(LoginRequest loginDTO);

    TokenResponse loginWithGoogle(GoogleLoginRequest loginWithGoogleRequest) throws GeneralSecurityException, IOException;


}
