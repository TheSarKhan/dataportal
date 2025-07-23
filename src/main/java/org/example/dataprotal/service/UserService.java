package org.example.dataprotal.service;

import jakarta.mail.MessagingException;
import jakarta.security.auth.message.AuthException;
import org.example.dataprotal.dto.request.ChangeSubscriptionRequest;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.response.*;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User getByEmail(String email);

    User getCurrentUser() throws AuthException;

    ProfileResponse getCurrentProfile() throws AuthException;

    List<UserResponseForAdmin> getAllUsers() throws AuthException;

    UserResponseForAdmin getUserById(Long id) throws AuthException;

    UserResponseForAdmin getUserByEmail(String email) throws AuthException;

    User getById(Long id);

    List<UserResponseForAdmin> searchUserByName(String name);

    ProfileSettingsResponse getProfileSettings() throws AuthException;

    ProfileSecurityResponse getProfileSecurity() throws AuthException;

    String  updateProfile(ProfileUpdateRequest profileUpdateRequest,
                                  MultipartFile profileImage) throws AuthException, IOException, MessagingException;

    ProfileResponse updateLanguage(String language) throws AuthException;

    String deactivateProfile(String deactivateReason) throws AuthException;

    UserResponseForAdmin deactivateUserWithId(Long id, String reason) throws AuthException;

    UserResponseForAdmin deactivateUserWithEmail(String email, String reason) throws AuthException;

    UserResponseForAdmin activateUserWithId(Long id) throws AuthException;

    UserResponseForAdmin activateUserWithEmail(String email) throws AuthException;

    UserResponseForAdmin changeUserRole(Long id, String role) throws AuthException;

    String changeSubscription(ChangeSubscriptionRequest request, PayriffInvoiceRequest paymentRequest) throws AuthException, InvoiceCanNotBeCreatedException;

    ProfileSecurityResponse updateSecurity(ProfileSecurityRequest request) throws AuthException;
}
