package com.lynqo.backend.presence.api;

import com.lynqo.backend.presence.dto.UserActivityRequest;
import com.lynqo.backend.presence.service.PresenceService;
import com.lynqo.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/getUserStatus")
    public ResponseEntity<?> getUserStatus(@RequestParam int userId) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "User status retrieved successfully");
            result.put("data", presenceService.getUserStatus(userId));
            return ResponseEntity.ok(result);
        } catch (Exception exception) {
            result.put("success", false);
            result.put("message", "Could not retrieve user status");
            result.put("data", null);
            log.error("Could not retrieve status for user {}", userId, exception);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
    }

    @PostMapping("/changeUserStatus")
    public ResponseEntity<?> changeUserStatus(@RequestParam int userId,
                                              @RequestParam String status,
                                              @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            presenceService.setUserStatus(currentUser.getId(), status);
            result.put("success", true);
            result.put("message", "User status changed successfully");
            result.put("data", currentUser.getId());
            return ResponseEntity.ok(result);
        } catch (Exception exception) {
            result.put("success", false);
            result.put("message", "Could not change user status");
            result.put("data", null);
            log.error("Could not change status for user {}", currentUser.getId(), exception);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
    }

    @PostMapping(value = "/userActivity", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserActivity(@RequestBody UserActivityRequest request,
                                                   @AuthenticationPrincipal User currentUser) {
        presenceService.setUserStatus(currentUser.getId(), request.getStatus());
        return ResponseEntity.ok().build();
    }
}
