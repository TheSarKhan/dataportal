package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Header Controller", description = "Provides header-related information for authenticated users")
public class HeaderController {
    private final HeaderService headerService;

    @GetMapping
    @Operation(summary = "Get header data", description = "Returns header information such as language, profile image, etc.")
    public ResponseEntity<HeaderResponse> getHeader() throws AuthException {
        return ResponseEntity.ok(headerService.getHeader());
    }
}
