package org.example.dataprotal.jwt.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
}