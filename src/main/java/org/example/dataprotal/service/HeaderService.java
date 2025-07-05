package org.example.dataprotal.service;

import jakarta.security.auth.message.AuthException;
import org.example.dataprotal.dto.response.HeaderResponse;

public interface HeaderService {
    HeaderResponse getHeader() throws AuthException;
}
