package com.lynqo.backend.auth.security;

import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class TokenGenerator {

    public static final String TOKEN_TYPE_CLAIM = "token_type";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtEncoder accessTokenEncoder;

    @Qualifier("jwtRefreshTokenEncoder")
    private final JwtEncoder refreshTokenEncoder;

    public TokenGenerator(
            JwtEncoder accessTokenEncoder,
            @Qualifier("jwtRefreshTokenEncoder") JwtEncoder refreshTokenEncoder) {
        this.accessTokenEncoder = accessTokenEncoder;
        this.refreshTokenEncoder = refreshTokenEncoder;
    }

    private GeneratedToken createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("lynqo")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))
                .id(tokenId)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .build();

        String value = accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
        return new GeneratedToken(value, tokenId);
    }

    private GeneratedToken createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("lynqo")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(String.valueOf(user.getId()))
                .id(tokenId)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .build();

        String value = refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
        return new GeneratedToken(value, tokenId);
    }

    public TokenResponse createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass())
            );
        }

        TokenResponse tokenDTO = new TokenResponse();
        tokenDTO.setUserId(String.valueOf(user.getId()));
        GeneratedToken accessToken = createAccessToken(authentication);
        GeneratedToken refreshToken = createRefreshToken(authentication);
        tokenDTO.setAccessToken(accessToken.value());
        tokenDTO.setAccessTokenId(accessToken.id());
        tokenDTO.setRefreshToken(refreshToken.value());
        tokenDTO.setRefreshTokenId(refreshToken.id());

        return tokenDTO;
    }

    private record GeneratedToken(String value, String id) {
    }

}
