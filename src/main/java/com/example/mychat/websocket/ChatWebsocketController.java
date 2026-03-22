package com.example.mychat.websocket;

import com.example.mychat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebsocketController {

    /**
     * Maneja mensajes de chat públicos
     * El cliente envía a: /app/chat.send
     * El servidor responde a: /topic/public
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(
            @Payload ChatMessageDTO message,
            Principal principal) {

        log.info("Mensaje de {}: {}", principal.getName(), message.getContent());
        message.setSender(principal.getName());
        message.setTimestamp(LocalDateTime.now());
        message.setType("CHAT");

        return message;
    }

    /**
     * Maneja la entrada de un usuario a la sala
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessageDTO joinRoom(
            @Payload ChatMessageDTO message,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal) {

        log.info("{} se unió a la sala: {}", principal.getName(), message.getRoomId());

        // Guardar el username en la sesión WebSocket
        headerAccessor.getSessionAttributes().put("username", principal.getName());
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());

        // Crear mensaje de bienvenida
        message.setType("JOIN");
        message.setSender(principal.getName());
        message.setContent(principal.getName() + " se unió al chat");
        message.setTimestamp(LocalDateTime.now());

        return message;
    }

    /**
     * Ejemplo de mensaje privado
     * El cliente envía a: /app/chat.private
     */
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(
            @Payload ChatMessageDTO message,
            Principal principal) {

        String sender = principal.getName();
        log.info("Mensaje privado de {} para: {}", sender, message.getRoomId());

        // TODO: Enviar a usuario específico
        // messagingTemplate.convertAndSendToUser(recipient, "/queue/private", message);
    }


}
