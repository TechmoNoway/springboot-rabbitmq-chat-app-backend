package com.lynqo.backend.messaging.dto;

import com.lynqo.backend.messaging.domain.MediaType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MessageResponse {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String status;
    private MediaType mediaType;
    private String mediaUrl;
    private Timestamp timestamp;
}
