package com.example.mychat.security.ws;

import com.example.mychat.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthFilter implements ChannelInterceptor {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        try {

            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                    message,
                    StompHeaderAccessor.class
            );

            if (accessor == null) {
                return message;
            }

            StompCommand command = accessor.getCommand();
            if (StompCommand.CONNECT.equals(command)) {

                log.info("Procesando autenticación WebSocket");

                // 1. Extraer header Authorization
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader == null) {
                    throw new IllegalArgumentException("Se requiere token de autenticación");
                }

                if (!authHeader.startsWith("Bearer ")) {
                    throw new IllegalArgumentException("Formato inválido. Use: Bearer <token>");
                }

                // Extraer token
                String token = authHeader.substring(7);

                // Obtener username del token
                String username = jwtService.getUsernameFromToken(token);

                // cargar UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {

                    // crear objeto Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // asociar sesion al websocket
                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                log.info("Usuario autenticado en WebSocket: {}", username);
            }
        }catch (Exception e){
            System.out.println(e);
            log.error(e.getMessage());
        }

        return message;
    }
}
