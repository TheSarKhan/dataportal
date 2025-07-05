package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileResponseForHeader;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() throws AuthException {
        return ResponseEntity.ok(userService.getCurrentProfile());
    }

    @GetMapping("/header")
    public ResponseEntity<ProfileResponseForHeader> getHeaderProfile() throws AuthException {
        return ResponseEntity.ok(userService.getProfileDataForHeader());
    }

    @GetMapping("/settings")
    public ResponseEntity<ProfileSettingsResponse>  getProfileSettings() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSettings());
    }

    @GetMapping("/security")
    public ResponseEntity<ProfileSecurityResponse> getProfileSecurity() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSecurity());
    }

    @PatchMapping("/security")
    public ResponseEntity<ProfileSecurityResponse> updateSecurity(@RequestBody ProfileSecurityRequest request) throws AuthException{
        return ResponseEntity.ok(userService.updateSecurity(request));
    }

    @PatchMapping("/{language}")
    public ResponseEntity<ProfileResponse> updateLanguage(@PathVariable String language) throws AuthException {
        return ResponseEntity.ok(userService.updateLanguage(language));
    }

    @PatchMapping("/deactivate-profile/{deactivateReason}")
    public ResponseEntity<String> deactivateProfile(@PathVariable String deactivateReason) throws AuthException {
        return ResponseEntity.ok(userService.deactivateProfile(deactivateReason));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@RequestPart ProfileUpdateRequest profileUpdateRequest,
                                                         @RequestPart MultipartFile profileImage) throws AuthException, IOException {
        return ResponseEntity.ok(userService.updateProfile(profileUpdateRequest, profileImage));
    }
}
