package com.lynqo.backend.auth.api;

import com.lynqo.backend.auth.security.TokenGenerator;
import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.service.AuthService;
import com.lynqo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TokenGenerator tokenGenerator;

    private final AuthService authService;

    private final UserService userService;

    @Qualifier("jwtRefreshTokenAuthProvider")
    private final JwtAuthenticationProvider refreshTokenAuthProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest signupDTO) {
        HashMap<String, Object> result = new HashMap<>();

        if (userService.isUsernameExist(signupDTO.getUsername())) {
            result.put("success", false);
            result.put("message", "Username existed");
            result.put("data", "username-exists");
            return ResponseEntity.ok(result);
        } else  {
            try {
                result.put("success", true);
                result.put("message", "Register successfully");
                result.put("status", HttpStatus.OK.value());
                result.put("data", authService.register(signupDTO));
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "Register failed");
                result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.put("data", null);
                log.error("error: ", e);
                return ResponseEntity.ok(result);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginDTO) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Login successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", authService.login(loginDTO));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Login failed");
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenResponse tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/loginWithGoogle")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest loginWithGoogleDTO){
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "LoginWithGoogle successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", authService.loginWithGoogle(loginWithGoogleDTO));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "LoginWithGoogle failed");
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

}
