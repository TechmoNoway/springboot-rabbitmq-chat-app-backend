package com.lynqo.backend.messaging.realtime;

import com.lynqo.backend.messaging.dto.MessageRequest;
import com.lynqo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserService userService;

    @MessageMapping("/topic")
    public void receivePrivateMessage(@Payload MessageRequest message) {
        // Assuming userService.getUserById returns a user object with a username
        String username = userService.getUserById(message.getReceiverId()).getUsername();

        // Construct the destination topic for the user
        String userDestination = "/topic/" + username;

        // Send the message to the user-specific topic
        simpMessagingTemplate.convertAndSend(userDestination, message);

        // Optional: Logging for debugging purposes
        System.out.println("Sending to " + userDestination);
        System.out.println(message.toString());
    }

    @MessageMapping("/queue")
    public void handleSignaling(@Payload MessageRequest message) {
        // Assuming userService.getUserById returns a user object with a username
        String username = userService.getUserById(message.getReceiverId()).getUsername();

        // Construct the destination topic for the user
        String userDestination = "/queue/" + username;

        // Send the message to the user-specific topic
        simpMessagingTemplate.convertAndSend(userDestination, message);

        // Optional: Logging for debugging purposes
        System.out.println("Sending to " + userDestination);
        System.out.println(message.toString());
    }

}   
