package com.example.mychat.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TestWebSocketController {

    @MessageMapping("/static/test")  // El cliente envía a /app/test
    @SendTo("/topic/test")    // El servidor responde a /topic/test
    public String test(String message) {
        System.out.println("Mensaje recibido: " + message);
        return "Eco: " + message;  // Esto se envía a todos los suscriptores
    }
}
