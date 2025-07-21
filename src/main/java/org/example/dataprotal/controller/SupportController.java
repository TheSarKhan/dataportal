package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.ContactFormRequest;
import org.example.dataprotal.dto.response.ContactFormResponse;
import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;
import org.example.dataprotal.service.SupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Support", description = "Support-related operations including FAQ, user instructions, and contact form")
public class SupportController {
    private final SupportService supportService;

    @GetMapping("/{language}")
    @Operation(summary = "Get support categories",
            description = "Returns a list of available support categories.")
    public ResponseEntity<List<String>> getSupportCategories(@PathVariable String language){
        return ResponseEntity.ok(supportService.getCategories(language));
    }

    @GetMapping("/faq/{language}")
    @Operation(summary = "Get FAQ information",
            description = "Retrieves frequently asked questions and answers.")
    public ResponseEntity<FaqResponse> getFagInfo(@PathVariable String language){
        return ResponseEntity.ok(supportService.getFagInfo(language));
    }

    @GetMapping("/user-instruction/{language}")
    @Operation(summary = "Get user instructions",
            description = "Provides instructions or user guides for system usage.")
    public ResponseEntity<UserInstructionResponse> getUserInstruction(@PathVariable String language){
        return ResponseEntity.ok(supportService.getUserInstruction(language));
    }

    @GetMapping("/contact-form/{language}")
    @Operation(summary = "Get contact form",
            description = "Returns default values or structure for the contact form.")
    public ResponseEntity<ContactFormResponse> getContactForm(@PathVariable String language){
        return ResponseEntity.ok(supportService.getContactForm(language));
    }

    @PostMapping("/contact-form")
    @Operation(summary = "Send contact message",
            description = "Sends a message to the support team via the contact form.")
    public ResponseEntity<String> sendMessage(@RequestBody ContactFormRequest request){
        return ResponseEntity.ok(supportService.sendContactForm(request));
    }
}
