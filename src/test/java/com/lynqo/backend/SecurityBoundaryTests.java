package com.lynqo.backend;

import tools.jackson.databind.ObjectMapper;
import com.lynqo.backend.auth.dto.SignupRequest;
import com.lynqo.backend.auth.dto.TokenResponse;
import com.lynqo.backend.auth.service.AuthService;
import com.lynqo.backend.messaging.domain.Message;
import com.lynqo.backend.messaging.dto.MessageRequest;
import com.lynqo.backend.messaging.repository.MessageRepository;
import com.lynqo.backend.user.dto.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityBoundaryTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void protectedApiRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/getAllUsers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanOnlyReadOwnPrivateProfile() throws Exception {
        TokenResponse first = registerUser();
        TokenResponse second = registerUser();

        mockMvc.perform(get("/api/v1/users/getUserById")
                        .param("id", second.getUserId())
                        .header("Authorization", "Bearer " + first.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(Integer.parseInt(first.getUserId())))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void clientCannotUpdateAnotherUser() throws Exception {
        TokenResponse first = registerUser();
        TokenResponse second = registerUser();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .userId(Integer.parseInt(second.getUserId()))
                .username("secured-" + UUID.randomUUID())
                .email("secured@example.com")
                .build();

        mockMvc.perform(put("/api/v1/users/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + first.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Integer.parseInt(first.getUserId())));
    }

    @Test
    void refreshingRotatesAndRevokesThePreviousSession() throws Exception {
        TokenResponse original = registerUser();
        assertThat(objectMapper.writeValueAsString(original))
                .doesNotContain("accessTokenId", "refreshTokenId");
        TokenResponse rotated = authService.refresh(original.getRefreshToken());

        assertThatThrownBy(() -> authService.refresh(original.getRefreshToken()))
                .isInstanceOf(JwtException.class);
        authService.refresh(rotated.getRefreshToken());
    }

    @Test
    void senderIdentityAndMessageOwnershipComeFromAuthentication() throws Exception {
        TokenResponse sender = registerUser();
        TokenResponse forgedSender = registerUser();
        TokenResponse receiver = registerUser();
        MessageRequest request = new MessageRequest();
        request.setSenderId(Integer.parseInt(forgedSender.getUserId()));
        request.setReceiverId(Integer.parseInt(receiver.getUserId()));
        request.setContent("secured message");

        mockMvc.perform(post("/api/v1/messages/saveMessage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + sender.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.senderId").value(Integer.parseInt(sender.getUserId())));

        Message message = messageRepository.findAll().stream()
                .max((left, right) -> Integer.compare(left.getId(), right.getId()))
                .orElseThrow();
        mockMvc.perform(delete("/api/v1/messages/deleteMessage")
                        .param("messageId", String.valueOf(message.getId()))
                        .header("Authorization", "Bearer " + forgedSender.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    private TokenResponse registerUser() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        SignupRequest request = new SignupRequest();
        request.setUsername("user-" + suffix);
        request.setEmail(suffix + "@example.com");
        request.setPassword("StrongPassword!123");
        return authService.register(request);
    }
}
