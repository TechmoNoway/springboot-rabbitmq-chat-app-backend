package com.lynqo.backend.auth.service.impl;

import com.lynqo.backend.auth.domain.Token;
import com.lynqo.backend.auth.domain.TokenType;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.GoogleTokenInfoResponse;
import com.lynqo.backend.auth.dto.GoogleUserInfoResponse;
import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.repository.TokenRepository;
import com.lynqo.backend.auth.security.TokenGenerator;
import com.lynqo.backend.auth.service.AuthService;
import com.lynqo.backend.user.domain.User;
import com.lynqo.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final TokenRepository tokenRepository;
    private final UserManager userManager;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;
    private final RestClient.Builder restClientBuilder;

    @Qualifier("jwtRefreshTokenDecoder")
    private final JwtDecoder refreshTokenDecoder;

    @Value("${lynqo.security.google.client-id:}")
    private String googleClientId;

    public AuthServiceImpl(
            TokenRepository tokenRepository,
            UserManager userManager,
            TokenGenerator tokenGenerator,
            DaoAuthenticationProvider daoAuthenticationProvider,
            UserRepository userRepository,
            RestClient.Builder restClientBuilder,
            @Qualifier("jwtRefreshTokenDecoder") JwtDecoder refreshTokenDecoder) {
        this.tokenRepository = tokenRepository;
        this.userManager = userManager;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.restClientBuilder = restClientBuilder;
        this.refreshTokenDecoder = refreshTokenDecoder;
    }

    @Override
    @Transactional
    public TokenResponse register(SignupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .roleId(1)
                .isActive(true)
                .isBlocked(false)
                .build();
        userManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, null, user.getAuthorities());
        return rotateSession(user, authentication);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(), request.getPassword()));
        User user = (User) authentication.getPrincipal();
        return rotateSession(user, authentication);
    }

    @Override
    @Transactional
    public TokenResponse loginWithGoogle(GoogleLoginRequest request)
            throws GeneralSecurityException, IOException {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new BadCredentialsException("Google OAuth client ID is not configured");
        }

        GoogleTokenInfoResponse tokenInfo;
        GoogleUserInfoResponse userInfo;
        try {
            RestClient client = restClientBuilder.build();
            tokenInfo = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("oauth2.googleapis.com")
                            .path("/tokeninfo")
                            .queryParam("access_token", request.getAccessToken())
                            .build())
                    .retrieve()
                    .body(GoogleTokenInfoResponse.class);
            userInfo = client.get()
                    .uri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .header("Authorization", "Bearer " + request.getAccessToken())
                    .retrieve()
                    .body(GoogleUserInfoResponse.class);
        } catch (RestClientException exception) {
            throw new BadCredentialsException("Google access token is invalid", exception);
        }

        validateGoogleIdentity(tokenInfo, userInfo);
        String email = userInfo.getEmail().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> createGoogleUser(email, userInfo.getPicture()));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, null, user.getAuthorities());
        return rotateSession(user, authentication);
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        Jwt jwt = refreshTokenDecoder.decode(refreshToken);
        User user = userRepository.findById(Integer.parseInt(jwt.getSubject()));
        if (user == null || user.isBlocked() || !user.isActive()) {
            throw new BadCredentialsException("User is not allowed to authenticate");
        }
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, jwt, user.getAuthorities());
        return rotateSession(user, authentication);
    }

    @Override
    @Transactional
    public void logout(int userId) {
        revokeSessions(userId);
    }

    private TokenResponse rotateSession(User user, Authentication authentication) {
        revokeSessions(user.getId());
        TokenResponse response = tokenGenerator.createToken(authentication);
        tokenRepository.saveAll(List.of(
                newToken(response.getAccessTokenId(), TokenType.ACCESS, user.getId()),
                newToken(response.getRefreshTokenId(), TokenType.REFRESH, user.getId())
        ));
        return response;
    }

    private Token newToken(String tokenId, TokenType type, int userId) {
        return Token.builder()
                .token(tokenId)
                .tokenType(type)
                .userId(userId)
                .expired(false)
                .revoked(false)
                .build();
    }

    private void revokeSessions(int userId) {
        List<Token> activeTokens = tokenRepository.findAllByUserIdAndRevokedFalse(userId);
        activeTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(activeTokens);
    }

    private void validateGoogleIdentity(GoogleTokenInfoResponse tokenInfo, GoogleUserInfoResponse userInfo) {
        boolean valid = tokenInfo != null
                && userInfo != null
                && googleClientId.equals(tokenInfo.getAud())
                && Boolean.TRUE.equals(tokenInfo.getEmailVerified())
                && tokenInfo.getExpiresIn() != null
                && tokenInfo.getExpiresIn() > 0
                && userInfo.getEmail() != null
                && userInfo.getEmail().equalsIgnoreCase(tokenInfo.getEmail())
                && Boolean.TRUE.equals(userInfo.getEmail_verified());
        if (!valid) {
            throw new BadCredentialsException("Google identity could not be verified");
        }
    }

    private User createGoogleUser(String email, String avatarUrl) {
        String baseUsername = email.substring(0, email.indexOf('@'))
                .replaceAll("[^A-Za-z0-9._-]", "");
        if (baseUsername.isBlank()) {
            baseUsername = "user";
        }
        String username = baseUsername;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        User user = User.builder()
                .username(username)
                .password(UUID.randomUUID() + "-" + UUID.randomUUID())
                .email(email)
                .avatarUrl(avatarUrl)
                .roleId(1)
                .isActive(true)
                .isBlocked(false)
                .build();
        userManager.createUser(user);
        return user;
    }
}
