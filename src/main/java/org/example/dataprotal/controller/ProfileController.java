package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.*;
import org.example.dataprotal.model.paymenthistory.PaymentHistory;
import org.example.dataprotal.service.PaymentHistoryService;
import org.example.dataprotal.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    private final UserService userService;
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() throws AuthException {
        return ResponseEntity.ok(userService.getCurrentProfile());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseForAdmin>> getAllProfile() throws AuthException {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseForAdmin> getProfileById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/settings")
    public ResponseEntity<ProfileSettingsResponse> getProfileSettings() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSettings());
    }

    @GetMapping("/security")
    public ResponseEntity<ProfileSecurityResponse> getProfileSecurity() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSecurity());
    }

    @PatchMapping("/security")
    public ResponseEntity<ProfileSecurityResponse> updateSecurity(@RequestBody ProfileSecurityRequest request) throws AuthException {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-profile/id/{id}/reason/{reason}")
    public ResponseEntity<UserResponseForAdmin> deactivateProfileById(@PathVariable Long id,
                                                                      @PathVariable String reason) throws AuthException {
        return ResponseEntity.ok(userService.deactivateUserWithId(id, reason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-profile/id/{id}")
    public ResponseEntity<UserResponseForAdmin> activateProfileById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(userService.activateUserWithId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/change-role/id/{id}/new-role/{role}")
    public ResponseEntity<UserResponseForAdmin> changeRoleById(@PathVariable Long id,
                                                               @PathVariable String role) throws AuthException {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@RequestPart ProfileUpdateRequest profileUpdateRequest,
                                                         @RequestPart MultipartFile profileImage) throws AuthException, IOException {
        return ResponseEntity.ok(userService.updateProfile(profileUpdateRequest, profileImage));
    }

    @GetMapping("/getMyPayments")
    public ResponseEntity<List<PaymentHistory>> getMyPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<PaymentHistory> paymentHistoryByUserEmail = paymentHistoryService.getPaymentHistoryByUserEmail(email);
        return ResponseEntity.ok(paymentHistoryByUserEmail);
    }
}
