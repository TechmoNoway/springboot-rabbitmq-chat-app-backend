package com.lynqo.backend.messaging.service;

import com.lynqo.backend.messaging.dto.MessageRequest;
import com.lynqo.backend.messaging.dto.MessageResponse;
import com.lynqo.backend.messaging.domain.Message;

import java.util.List;

public interface MessageService {

    List<Message> getMessagesByUserId(int userId);

    List<Message> getMessagesByFriendId(int friendId);

    List<MessageResponse> getMessagesByFriendBoxChat(int user1Id, int user2Id);

    MessageResponse getTheLatestMessage(int user1Id, int user2Id);

    void saveMessage(MessageRequest messageRequest);

    void deleteMessage(int messageId);
}
