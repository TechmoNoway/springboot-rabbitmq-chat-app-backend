package com.lynqo.backend.messaging.realtime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${lynqo.messaging.relay.host}")
    private String relayHost;

    @Value("${lynqo.messaging.relay.port}")
    private int relayPort;

    @Value("${spring.rabbitmq.username}")
    private String relayUsername;

    @Value("${spring.rabbitmq.password}")
    private String relayPassword;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setClientLogin(relayUsername)
                .setClientPasscode(relayPassword)
                .setSystemLogin(relayUsername)
                .setSystemPasscode(relayPassword);
        registry.setUserDestinationPrefix("/queue");
    }
}
