package com.lynqo.backend.messaging.support;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class MessageParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parseTargetUserFromPayload(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            return rootNode.get("to").asString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
