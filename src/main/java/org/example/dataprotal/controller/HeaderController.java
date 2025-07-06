package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.response.HeaderResponse;
import org.example.dataprotal.service.HeaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/header")
@SecurityRequirement(name = "bearerAuth")
public class HeaderController {
    private final HeaderService headerService;

    @GetMapping
    public ResponseEntity<HeaderResponse> getHeader() throws AuthException {
        return ResponseEntity.ok(headerService.getHeader());
    }
}
