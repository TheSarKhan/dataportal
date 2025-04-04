package org.example.dataprotal.jwt.service.service;

import org.example.dataprotal.jwt.dto.request.LoginDTO;
import org.example.dataprotal.jwt.dto.request.RegisterDTO;
import org.example.dataprotal.jwt.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse register(RegisterDTO request);
    TokenResponse login(LoginDTO request);

}
