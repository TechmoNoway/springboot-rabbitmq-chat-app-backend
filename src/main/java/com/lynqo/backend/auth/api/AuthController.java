package com.lynqo.backend.auth.api;

import com.lynqo.backend.auth.dto.LoginRequest;
import com.lynqo.backend.auth.dto.GoogleLoginRequest;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.service.AuthService;
import com.lynqo.backend.user.service.UserService;
import com.lynqo.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest signupDTO) {
        HashMap<String, Object> result = new HashMap<>();

        if (userService.isUsernameExist(signupDTO.getUsername())) {
            result.put("success", false);
            result.put("message", "Username existed");
            result.put("data", "username-exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenResponse tokenDTO) {
        try {
            return ResponseEntity.ok(authService.refresh(tokenDTO.getRefreshToken()));
        } catch (Exception exception) {
            log.warn("Refresh token rejected", exception);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User currentUser) {
        authService.logout(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

}
