package com.lynqo.backend.messaging.service.impl;

import com.lynqo.backend.messaging.dto.MessageRequest;
import com.lynqo.backend.messaging.dto.MessageResponse;
import com.lynqo.backend.messaging.domain.Message;
import com.lynqo.backend.messaging.repository.MessageRepository;
import com.lynqo.backend.user.repository.UserRepository;
import com.lynqo.backend.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    @Override
    public List<Message> getMessagesByUserId(int userId) {
        return List.of();
    }

    @Override
    public List<Message> getMessagesByFriendId(int friendId) {
        return List.of();
    }

    @Override
    public List<MessageResponse> getMessagesByFriendBoxChat(int user1Id, int user2Id) {
        List<MessageResponse> result = messageRepository.findMessagesBetweenTwoUsers(user1Id, user2Id).stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .senderId(message.getSender().getId())
                        .receiverId(message.getReceiver().getId())
                        .content(message.getContent())
                        .status(message.getStatus())
                        .mediaType(message.getMediaType())
                        .mediaUrl(message.getMediaUrl())
                        .timestamp(message.getTimestamp())
                        .build())
                .collect(Collectors.toList());


        result.sort(Comparator.comparing(MessageResponse::getTimestamp));
        return result;
    }

    @Override
    public MessageResponse getTheLatestMessage(int user1Id, int user2Id) {

        List<MessageResponse> result = getMessagesByFriendBoxChat(user1Id, user2Id);

        if (result.isEmpty()) {
            return null;
        }

        return result.getLast();
    }

    @Override
    public void saveMessage(MessageRequest messageRequest, int authenticatedUserId) {

        Message message = Message.builder()
                .sender(userRepository.findById(authenticatedUserId))
                .receiver(userRepository.findById(messageRequest.getReceiverId()))
                .content(messageRequest.getContent())
                .status(messageRequest.getStatus())
                .mediaType(messageRequest.getMediaType())
                .mediaUrl(messageRequest.getMediaUrl())
                .timestamp(messageRequest.getTimestamp())
                .build();
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(int messageId, int authenticatedUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        if (message.getSender().getId() != authenticatedUserId) {
            throw new AccessDeniedException("Only the sender can delete a message");
        }
        messageRepository.delete(message);
    }

}
