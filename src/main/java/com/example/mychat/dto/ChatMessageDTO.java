package com.example.mychat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private String type;        // "CHAT", "JOIN", "LEAVE"
    private String content;     // Contenido del mensaje
    private String roomId;      // Sala del chat
    private String sender;
    private LocalDateTime timestamp;

}
