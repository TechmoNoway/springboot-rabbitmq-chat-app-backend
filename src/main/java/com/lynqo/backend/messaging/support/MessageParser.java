package com.lynqo.backend.messaging.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parseTargetUserFromPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            return rootNode.get("to").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}