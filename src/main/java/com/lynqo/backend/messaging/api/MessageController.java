package com.lynqo.backend.messaging.api;

import com.lynqo.backend.messaging.dto.MessageRequest;
import com.lynqo.backend.messaging.service.MessageService;
import com.lynqo.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/getMessageBetweenTwoUsers")
    public ResponseEntity<?> doGetMessageBetweenTwoUsers(@RequestParam("user1Id") int user1Id,
                                                         @RequestParam("user2Id") int user2Id,
                                                         @AuthenticationPrincipal User currentUser) {
        int friendId = otherParticipant(currentUser.getId(), user1Id, user2Id);
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api doGetMessagesByFriendBoxChat successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", messageService.getMessagesByFriendBoxChat(currentUser.getId(), friendId));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doGetMessagesByFriendBoxChat failed");
            result.put("status", HttpStatus.NO_CONTENT.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/getTheLatestMessage")
    public ResponseEntity<?> doGetTheLatestMessage(@RequestParam("user1Id") int user1Id,
                                                   @RequestParam("user2Id") int user2Id,
                                                   @AuthenticationPrincipal User currentUser) {
        int friendId = otherParticipant(currentUser.getId(), user1Id, user2Id);
        HashMap<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Call api doGetTheLatestMessage successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", messageService.getTheLatestMessage(currentUser.getId(), friendId));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doGetTheLatestMessage failed");
            result.put("status", HttpStatus.NO_CONTENT.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/saveMessage")
    public ResponseEntity<?> doSaveMessage(@RequestBody MessageRequest messageRequest,
                                           @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            messageRequest.setSenderId(currentUser.getId());
            messageService.saveMessage(messageRequest, currentUser.getId());
            result.put("success", true);
            result.put("message", "Call api doSaveMessage successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", messageRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doSaveMessage failed");
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/deleteMessage")
    public ResponseEntity<?> doDeleteMessage(@RequestParam("messageId") int messageId,
                                             @AuthenticationPrincipal User currentUser) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            messageService.deleteMessage(messageId, currentUser.getId());
            result.put("success", true);
            result.put("message", "Call api doDeleteMessage successfully");
            result.put("status", HttpStatus.OK.value());
            result.put("data", messageId);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Call api doDeleteMessage failed");
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("data", null);
            log.error("error: ", e);
            return ResponseEntity.ok(result);
        }
    }

    private int otherParticipant(int currentUserId, int user1Id, int user2Id) {
        if (user1Id == currentUserId) {
            return user2Id;
        }
        if (user2Id == currentUserId) {
            return user1Id;
        }
        throw new AccessDeniedException("The authenticated user must be a conversation participant");
    }

}
