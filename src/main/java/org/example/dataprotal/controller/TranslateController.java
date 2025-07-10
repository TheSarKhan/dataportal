package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.service.TranslateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/translate")
@SecurityRequirement( name = "bearerAuth")
public class TranslateController {
    private final TranslateService translateService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/retranslate-files")
    public ResponseEntity<String> retranslateFiles() throws IOException, InterruptedException {
        return ResponseEntity.ok(translateService.translateFiles());
    }
}
