package org.example.dataprotal.service.impl;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.SubscriptionDataDto;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileResponseForHeader;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.enums.Subscription;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.service.CloudinaryService;
import org.example.dataprotal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final CloudinaryService cloudinaryService;

    @Value("${profile.deactivate-reason.aze}")
    String deactivateReasonAze;

    @Value("${profile.deactivate-reason.ru}")
    String deactivateReasonRu;

    @Value("${profile.deactivate-reason.en}")
    String deactivateReasonEn;


    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found with email {}", email);
            return new NoSuchElementException("User not found with email " + email);
        });
    }

    @Override
    public User getCurrentUser() throws AuthException {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email.equals("anonymousUser")) {
            log.warn("User doesn't login!!!");
            throw new AuthException("User doesn't login!!!");
        }
        return getByEmail(email);
    }

    @Override
    public ProfileResponse getCurrentProfile() throws AuthException {
        log.info("Get current profile : {}", getCurrentUser().getFirstName());
        return mappedUserToProfileResponse(getCurrentUser());
    }

    @Override
    public ProfileResponseForHeader getProfileDataForHeader() throws AuthException {
        User currentUser = getCurrentUser();
        return new ProfileResponseForHeader(currentUser.getLanguage(), currentUser.getProfileImage());
    }

    @Override
    public ProfileSettingsResponse getProfileSettings() throws AuthException {
        User currentUser = getCurrentUser();

        Map<Language, String> languages = new HashMap<>();
        for (Language language : Language.values()) {
            languages.put(language, language.getFullName());
        }

        return new ProfileSettingsResponse(
                currentUser.getLanguage(),
                languages,
                currentUser.getSubscription(),
                getSubscriptions(currentUser),
                getDeactivateReasons(currentUser));
    }

    @Override
    public ProfileSecurityResponse getProfileSecurity() throws AuthException {
        User currentUser = getCurrentUser();
        return new ProfileSecurityResponse(
                currentUser.getRecoveryEmail(),
                currentUser.getRecoveryPhoneNumber());
    }

    @Override
    public ProfileResponse updateProfile(ProfileUpdateRequest request,
                                         MultipartFile profileImage) throws AuthException, IOException {
        log.info("Update profile : {}", request);
        User currentUser = getCurrentUser();
        String url = cloudinaryService.uploadFile(profileImage, "4sim-profileImage");
        currentUser.setProfileImage(url);
        currentUser.setEmail(request.email());
        currentUser.setPhoneNumber(request.phoneNumber());
        currentUser.setWorkplace(request.workplace());
        currentUser.setPosition(request.position());
        return mappedUserToProfileResponse(userRepository.save(currentUser));
    }

    @Override
    public ProfileResponse updateLanguage(String language) throws AuthException {
        log.info("Update language : {}", language);
        User user = getCurrentUser();
        user.setLanguage(Language.valueOf(language.toUpperCase()));
        return mappedUserToProfileResponse(userRepository.save(user));
    }

    @Override
    public String deactivateProfile(String deactivateReason) throws AuthException {
        User currentUser = getCurrentUser();
        currentUser.setActive(false);
        currentUser.setDeactivateTime(LocalDateTime.now());
        currentUser.setDeactivateReason(deactivateReason);
        userRepository.save(currentUser);
        return "Deactivation is successfully.";
    }

    @Override
    public ProfileSecurityResponse updateSecurity(ProfileSecurityRequest request) throws AuthException {
        User currentUser = getCurrentUser();
        if (!StringUtil.isBlank(request.oldPassword()) &&
                passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())){
            currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        if (!StringUtil.isBlank(request.recoveryEmail()) ||
                !request.recoveryEmail().equals(currentUser.getRecoveryEmail()) ){
            if (currentUser.getEmail().equals(request.recoveryEmail())){
                log.warn("Email is same with current user's email.");
                throw new RuntimeException("Email is same with current user's email. Please change email or recovery email. (Email: " + request.recoveryEmail() + " )");
            }
            currentUser.setRecoveryEmail(request.recoveryEmail());
        }
        if (!StringUtil.isBlank(request.recoveryPhoneNumber()) ||
                !request.recoveryPhoneNumber().equals(currentUser.getRecoveryPhoneNumber()) ){
            if (currentUser.getPhoneNumber().equals(request.recoveryPhoneNumber())){
                log.warn("Phone number is same with current user's phone number.");
                throw new RuntimeException("Phone number is same with current user's phone number. Please change phone number or recovery phone number. (phone number: " + request.recoveryPhoneNumber() + " )");
            }
            currentUser.setRecoveryPhoneNumber(request.recoveryPhoneNumber());
        }
        User user = userRepository.save(currentUser);
        return new ProfileSecurityResponse(
                user.getRecoveryEmail(),
                user.getRecoveryPhoneNumber());
    }

    private Map<Subscription, SubscriptionDataDto> getSubscriptions(User currentUser) {
        Map<Subscription, SubscriptionDataDto> subscriptions = new HashMap<>();
        for (Subscription subscription : Subscription.values()) {
            subscriptions.put(subscription,
                    new SubscriptionDataDto(subscription.getPriceForOneMonth(),
                            subscription.getPriceForOneYear(),
                            subscription.getAdvantages().get(currentUser.getLanguage())));
        }
        return subscriptions;
    }

    private List<String> getDeactivateReasons(User currentUser) {
        String deactivateReason = switch (currentUser.getLanguage()) {
            case AZE -> deactivateReasonAze;
            case EN -> deactivateReasonEn;
            case RU -> deactivateReasonRu;
        };
        return Arrays.stream(deactivateReason.split("\\.")).toList();
    }

    private ProfileResponse mappedUserToProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getProfileImage(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getWorkplace(),
                user.getPosition());
    }
}
