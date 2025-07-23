package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.ChangeSubscriptionRequest;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.dto.response.UserResponseForAdmin;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile Controller",
        description = "APIs for managing user profile, settings, roles and activation status")
public class ProfileController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get current user profile",
            description = "Returns the profile information of the currently authenticated user")
    public ResponseEntity<ProfileResponse> getProfile() throws AuthException {
        return ResponseEntity.ok(userService.getCurrentProfile());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all profiles",
            description = "Returns profile information of all users (Admin only)")
    public ResponseEntity<List<UserResponseForAdmin>> getAllProfile() throws AuthException {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profile by ID",
            description = "Returns profile information for a specific user by ID (Admin only)")
    public ResponseEntity<UserResponseForAdmin> getProfileById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profile by email",
            description = "Returns profile information for a specific user by email (Admin only)")
    public ResponseEntity<UserResponseForAdmin> getProfileByEmail(@PathVariable String email) throws AuthException {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/settings")
    @Operation(summary = "Get profile settings",
            description = "Returns profile settings of the current user")
    public ResponseEntity<ProfileSettingsResponse> getProfileSettings() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSettings());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/name/{name}")
    @Operation(summary = "Search profile by name",
            description = "Returns profile of the entered name and similar ones")
    public ResponseEntity<List<UserResponseForAdmin>> searchProfileByName(@PathVariable String name){
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    @GetMapping("/security")
    @Operation(summary = "Get profile security",
            description = "Returns security information of the current user")
    public ResponseEntity<ProfileSecurityResponse> getProfileSecurity() throws AuthException {
        return ResponseEntity.ok(userService.getProfileSecurity());
    }

    @PatchMapping("/security")
    @Operation(summary = "Update profile security",
            description = "Allows user to update security-related information like password")
    public ResponseEntity<ProfileSecurityResponse> updateSecurity(@RequestBody ProfileSecurityRequest request) throws AuthException {
        return ResponseEntity.ok(userService.updateSecurity(request));
    }

    @PatchMapping("/{language}")
    @Operation(summary = "Change language",
            description = "Updates preferred language of the current user")
    public ResponseEntity<ProfileResponse> updateLanguage(@PathVariable String language) throws AuthException {
        return ResponseEntity.ok(userService.updateLanguage(language));
    }

    @PatchMapping("/deactivate-profile/{deactivateReason}")
    @Operation(summary = "Deactivate current profile",
            description = "Deactivates the current user's profile with a reason")
    public ResponseEntity<String> deactivateProfile(@PathVariable String deactivateReason) throws AuthException {
        return ResponseEntity.ok(userService.deactivateProfile(deactivateReason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-profile/id/{id}/reason/{reason}")
    @Operation(summary = "Deactivate profile by ID",
            description = "Deactivates a user profile by ID with given reason (Admin only)")
    public ResponseEntity<UserResponseForAdmin> deactivateProfileById(@PathVariable Long id,
                                                                      @PathVariable String reason) throws AuthException {
        return ResponseEntity.ok(userService.deactivateUserWithId(id, reason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-profile/email/{email}/reason/{reason}")
    @Operation(summary = "Deactivate profile by email",
            description = "Deactivates a user profile by email with given reason (Admin only)")
    public ResponseEntity<UserResponseForAdmin> deactivateProfileByEmail(@PathVariable String email,
                                                                         @PathVariable String reason) throws AuthException {
        return ResponseEntity.ok(userService.deactivateUserWithEmail(email, reason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-profile/id/{id}")
    @Operation(summary = "Activate profile by ID",
            description = "Activates a user profile by ID (Admin only)")
    public ResponseEntity<UserResponseForAdmin> activateProfileById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(userService.activateUserWithId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-profile/email/{email}")
    @Operation(summary = "Activate profile by email",
            description = "Activates a user profile by email (Admin only)")
    public ResponseEntity<UserResponseForAdmin> activateProfileByEmail(@PathVariable String email) throws AuthException {
        return ResponseEntity.ok(userService.activateUserWithEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/change-role/id/{id}/new-role/{role}")
    @Operation(summary = "Change role",
            description = "Changes role of a user by ID (Admin only)")
    public ResponseEntity<UserResponseForAdmin> changeRoleById(@PathVariable Long id,
                                                               @PathVariable String role) throws AuthException {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

    @PostMapping("/change-subscription")
    @Operation(
            summary = "Change user subscription",
            description = "Changes the user's subscription plan and initiates payment via Payriff."
    )
    public ResponseEntity<String> changeSubscription(@RequestBody ChangeSubscriptionRequest request,
                                                     @RequestBody PayriffInvoiceRequest paymentRequest) throws AuthException, InvoiceCanNotBeCreatedException {
        return ResponseEntity.ok(userService.changeSubscription(request, paymentRequest));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update profile",
            description = "Updates profile information along with profile image")
    public ResponseEntity<String> updateProfile(@RequestPart ProfileUpdateRequest profileUpdateRequest,
                                                         @RequestPart MultipartFile profileImage) throws AuthException, IOException, MessagingException {
        return ResponseEntity.ok(userService.updateProfile(profileUpdateRequest, profileImage));
    }
}
