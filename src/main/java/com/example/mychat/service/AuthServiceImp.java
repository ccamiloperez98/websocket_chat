package com.example.mychat.service;

import com.example.mychat.dto.AuthResponseDTO;
import com.example.mychat.dto.LoginRequestDTO;
import com.example.mychat.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();

        String token = jwtService.getToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }
}
