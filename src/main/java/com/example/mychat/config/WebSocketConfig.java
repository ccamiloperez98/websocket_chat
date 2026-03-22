package com.example.mychat.config;

import com.example.mychat.security.ws.WebSocketAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Habilita el broker de mensajes STOMP
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthFilter webSocketAuthFilter;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthFilter);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para destinos donde el servidor envía mensajes (broadcast)
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefijo para mensajes que el cliente envía al servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijo para mensajes privados por usuario
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("http://localhost:8080","http://localhost:63342")
                .withSockJS();
    }

}
