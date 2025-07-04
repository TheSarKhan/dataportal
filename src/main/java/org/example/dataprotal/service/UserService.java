package org.example.dataprotal.service;

import jakarta.security.auth.message.AuthException;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileResponseForHeader;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    User getByEmail(String email);

    User getCurrentUser() throws AuthException;

    ProfileResponse getCurrentProfile() throws AuthException;

    ProfileResponseForHeader getProfileDataForHeader() throws AuthException;

    ProfileSettingsResponse getProfileSettings() throws AuthException;

    ProfileSecurityResponse getProfileSecurity() throws AuthException;

    ProfileResponse updateProfile(ProfileUpdateRequest profileUpdateRequest,
                                  MultipartFile profileImage) throws AuthException, IOException;

    ProfileResponse updateLanguage(String language) throws AuthException;

    String deactivateProfile(String deactivateReason) throws AuthException;

    ProfileSecurityResponse updateSecurity(ProfileSecurityRequest request) throws AuthException;

}
