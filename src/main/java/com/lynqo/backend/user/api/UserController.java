package com.lynqo.backend.user.api;

import com.lynqo.backend.user.dto.UpdateUserRequest;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> doGetAllUsers() {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api getAllUsers successfully");
            result.put("data", userService.getAllUsers());
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api getAllUsers failed");
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/getUserById")
    public ResponseEntity<?> doGetUserById(@RequestParam("id") int ignoredId,
                                           @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api getUserById successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", userService.getUserById(currentUser.getId()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api getUserById failed");
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/searchUserByString")
    public ResponseEntity<?> doSearchUserByString(@RequestParam("str") String str) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api searchUserByString successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", userService.searchUserByString(str));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api searchUserByString failed");
            result.put("status", HttpStatus.NO_CONTENT.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> doUpdateUser(@RequestBody UpdateUserRequest updateUserDTO,
                                          @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            updateUserDTO.setUserId(currentUser.getId());
            result.put("success", true);
            result.put("message", "Call api doUpdateUser successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", userService.updateUser(updateUserDTO));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doUpdateUser failed");
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/getFriendsAndLatestMessage")
    public ResponseEntity<?> doGetFriendsAndLatestMessage(@RequestParam("userId") int ignoredUserId,
                                                          @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api doGetFriendsAndLatestMessage successfully");
            result.put("data", userService.getAllUserFriendsAndLatestMessage(currentUser.getId()));
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doGetFriendsAndLatestMessage failed");
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

}
