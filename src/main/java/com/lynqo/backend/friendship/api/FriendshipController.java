package com.lynqo.backend.friendship.api;

import com.lynqo.backend.friendship.service.FriendshipService;
import com.lynqo.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/friend")
public class FriendshipController {

    private final FriendshipService friendService;

    @GetMapping("/checkIsFriend")
    public ResponseEntity<?> doCheckIsFriend(@RequestParam("userId") int ignoredUserId,
                                             @RequestParam("friendId") int friendId,
                                             @AuthenticationPrincipal User currentUser){
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api checkIsFriend successfully");
            result.put("data", friendService.checkIsFriend(currentUser.getId(), friendId));
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api checkIsFriend failed");
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/addFriend")
    public ResponseEntity<?> doAddFriend(@RequestParam("userId") int ignoredUserId,
                                         @RequestParam("friendId") int friendId,
                                         @AuthenticationPrincipal User currentUser){
        HashMap<String, Object> result = new HashMap<>();
        try {
            int userId = currentUser.getId();
            friendService.addFriend(userId, friendId, "accepted");
            result.put("success", true);
            result.put("message", "Call api addFriend successfully");
            result.put("data", userId +  "" + friendId);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api addFriend failed");
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @DeleteMapping("/removeFriend")
    public ResponseEntity<?> doRemoveFriend(@RequestParam("userId") int ignoredUserId,
                                            @RequestParam("friendId") int friendId,
                                            @AuthenticationPrincipal User currentUser){
        HashMap<String, Object> result = new HashMap<>();
        try {
            int userId = currentUser.getId();
            friendService.removeFriend(userId, friendId);
            result.put("success", true);
            result.put("message", "Call api removeFriend successfully");
            result.put("data", userId +  "" + friendId);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api removeFriend failed");
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
