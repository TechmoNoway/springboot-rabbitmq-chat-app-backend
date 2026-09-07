package com.lynqo.backend.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynqo.backend.auth.security.TokenGenerator;
import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.dto.GoogleUserInfoResponse;
import com.lynqo.backend.auth.domain.Token;
import com.lynqo.backend.user.domain.User;
import com.lynqo.backend.auth.repository.TokenRepository;
import com.lynqo.backend.user.repository.UserRepository;
import com.lynqo.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenRepository tokenRepository;
    private final UserManager userManager;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;

    public void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
//                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenResponse register(SignupRequest signupDTO) {
        User user = User.builder()
                .username(signupDTO.getUsername())
                .password(signupDTO.getPassword())
                .email(signupDTO.getEmail())
                .roleId(1)
                .isActive(true)
                .isBlocked(false)
                .build();
        userManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), Collections.emptyList());
        TokenResponse tokenDTO = tokenGenerator.createToken(authentication);
        saveUserToken(user, tokenDTO.getAccessToken());

        return tokenDTO;
    }

    @Override
    public TokenResponse login(LoginRequest loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));

        return tokenGenerator.createToken(authentication);
    }

    @Override
    public TokenResponse loginWithGoogle(GoogleLoginRequest loginWithGoogleRequest) throws GeneralSecurityException, IOException {
        String apiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        URL url = URI.create(apiUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + loginWithGoogleRequest.getAccessToken());

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            GoogleUserInfoResponse googleUserInfoResponse = objectMapper.readValue(response.toString(), GoogleUserInfoResponse.class);

            String email = googleUserInfoResponse.getEmail();
            String avatarUrl = googleUserInfoResponse.getPicture();
            String name = googleUserInfoResponse.getName();

            int index = email.indexOf('@');
            if (!userRepository.existsByUsername(email.substring(0, index))) {
                User user = User.builder()
                        .username(email.substring(0, index))
                        .password(email)
                        .email(email)
                        .avatarUrl(avatarUrl)
                        .roleId(1)
                        .isActive(true)
                        .isBlocked(false)
                        .build();
                userManager.createUser(user);

                Authentication registingAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), Collections.emptyList());
                TokenResponse tokenDTO = tokenGenerator.createToken(registingAuthentication);

                Authentication loginAuthentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email.substring(0, index), email));
                return tokenGenerator.createToken(loginAuthentication);
            } else {
                Optional<User> user = userRepository.findByUsername(email.substring(0, index));
                Authentication loginAuthentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email.substring(0, index), user.get().getEmail()));
                return tokenGenerator.createToken(loginAuthentication);
            }
        } else {
            return null;
        }
    }
}
