package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.response.ContactFormResponse;
import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;
import org.example.dataprotal.service.SupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support")
@SecurityRequirement(name = "bearerAuth")
public class SupportController {
    private final SupportService supportService;

    @GetMapping
    public ResponseEntity<List<String>> getSupportCategories(){
        return ResponseEntity.ok(supportService.getCategories());
    }

    @GetMapping("/faq")
    public ResponseEntity<FaqResponse> getFagInfo(){
        return ResponseEntity.ok(supportService.getFagInfo());
    }

    @GetMapping("/user-instruction")
    public ResponseEntity<UserInstructionResponse> getUserInstruction(){
        return ResponseEntity.ok(supportService.getUserInstruction());
    }
}
