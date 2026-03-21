package com.example.mychat.service;

import com.example.mychat.dto.AuthResponseDTO;
import com.example.mychat.dto.LoginRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);
}
