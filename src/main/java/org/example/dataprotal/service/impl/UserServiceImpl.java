package org.example.dataprotal.service.impl;

import com.cloudinary.utils.StringUtils;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.example.dataprotal.config.RecaptchaConfig;
import org.example.dataprotal.dto.SubscriptionDataDto;
import org.example.dataprotal.dto.request.ChangeSubscriptionRequest;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.dto.response.UserResponseForAdmin;
import org.example.dataprotal.enums.*;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.payment.service.PayriffService;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.service.CloudinaryService;
import org.example.dataprotal.service.PaymentHistoryService;
import org.example.dataprotal.service.TranslateService;
import org.example.dataprotal.service.UserService;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.dataprotal.mapper.UserMapper.userToProfileResponse;
import static org.example.dataprotal.mapper.UserMapper.userToUserResponseForAdmin;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final CloudinaryService cloudinaryService;

    private final TranslateService translateService;

    private final ReloadableResourceBundleMessageSource messageSource;

    private final PayriffService payriffService;

    private final PaymentHistoryService paymentHistoryService;

    private final RecaptchaConfig recaptchaConfig;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found with email : {}", email);
            return new NoSuchElementException("User not found with email : " + email);
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
        return userToProfileResponse(getCurrentUser());
    }

    @Override
    public List<UserResponseForAdmin> getAllUsers() throws AuthException {
        log.info("Get all users");
        User admin = getCurrentUser();
        return userRepository.findAll().stream()
                .map(user -> getUserResponseForAdminTranslated(user, admin)).toList();
    }

    @Override
    public UserResponseForAdmin getUserById(Long id) throws AuthException {
        log.info("Get user by id : {}", id);
        return getUserResponseForAdminTranslated(getById(id), getCurrentUser());
    }

    @Override
    public UserResponseForAdmin getUserByEmail(String email) throws AuthException {
        log.info("Get user by email : {}", email);
        return getUserResponseForAdminTranslated(getByEmail(email), getCurrentUser());
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id : {}", id);
            return new NoSuchElementException("User not found with id : " + id);
        });
    }

    @Override
    public ProfileSettingsResponse getProfileSettings() throws AuthException {
        log.info("Get profile settings");
        User currentUser = getCurrentUser();

        Map<Language, String> languages = new HashMap<>();
        for (Language language : Language.values()) {
            languages.put(language, language.getFullName());
        }

        String deactivateReason = messageSource.getMessage("deactivate-reasons", null,
                new Locale(currentUser.getLanguage().name().toLowerCase()));
        List<String> deactivateReasons = Arrays.stream(deactivateReason.split("\\.")).map(String::trim).toList();

        return new ProfileSettingsResponse(
                currentUser.getLanguage(),
                languages,
                currentUser.getSubscription(),
                getSubscriptions(currentUser),
                deactivateReasons);
    }

    @Override
    public ProfileSecurityResponse getProfileSecurity() throws AuthException {
        log.info("Get profile security");
        User currentUser = getCurrentUser();
        return new ProfileSecurityResponse(
                currentUser.getRecoveryEmail(),
                currentUser.getRecoveryPhoneNumber());
    }

    @Override
    public List<UserResponseForAdmin> searchUserByName(String name) throws AuthException {
        User admin = getCurrentUser();
        return userRepository.searchUserByName(name).stream()
                .map(user -> getUserResponseForAdminTranslated(user, admin)).toList();
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
        currentUser.setUpdatedAt(LocalDateTime.now());
        return userToProfileResponse(userRepository.save(currentUser));
    }

    @Override
    public ProfileResponse updateLanguage(String language) throws AuthException {
        log.info("Update language : {}", language);
        User user = getCurrentUser();
        if (user.getPosition() != null)
            user.setPosition(translateService.translate(
                    user.getLanguage().name().toLowerCase(),
                    language.toLowerCase(),
                    user.getPosition()));
        user.setLanguage(Language.valueOf(language.toUpperCase()));
        return userToProfileResponse(userRepository.save(user));
    }

    @Override
    public String deactivateProfile(String deactivateReason) throws AuthException {
        log.info("Deactivate profile : {}", deactivateReason);
        User currentUser = getCurrentUser();
        currentUser.setActive(false);
        currentUser.setDeactivateTime(LocalDateTime.now());
        currentUser.setDeactivateReason(deactivateReason);
        userRepository.save(currentUser);
        return messageSource.getMessage("user.deactivated", null,
                new Locale(currentUser.getLanguage().name().toLowerCase()));
    }

    @Override
    public UserResponseForAdmin deactivateUserWithId(Long id, String reason) throws AuthException {
        log.info("Deactivate user with id : {}", id);
        User user = getById(id);
        return deactivateUser(reason, user);
    }

    @Override
    public UserResponseForAdmin deactivateUserWithEmail(String email, String reason) throws AuthException {
        log.info("Deactivate user with email : {}", email);
        User user = getByEmail(email);
        return deactivateUser(reason, user);
    }

    @Override
    public UserResponseForAdmin activateUserWithId(Long id) throws AuthException {
        log.info("Activate user with id : {}", id);
        User user = getById(id);
        return activateUser(user);
    }

    @Override
    public UserResponseForAdmin activateUserWithEmail(String email) throws AuthException {
        log.info("Activate user with email : {}", email);
        User user = getByEmail(email);
        return activateUser(user);
    }

    @Override
    public UserResponseForAdmin changeUserRole(Long id, String role) throws AuthException {
        log.info("Change user role with id : {} to {}", id, role);
        User user = getById(id);
        user.setRole(Role.valueOf(role.toUpperCase()));
        return getUserResponseForAdminTranslated(userRepository.save(user), getCurrentUser());
    }

    @Override
    @Transactional
    public String changeSubscription(ChangeSubscriptionRequest request, PayriffInvoiceRequest paymentRequest) throws AuthException, InvoiceCanNotBeCreatedException {
        String subscription = request.subscription();
        String recaptchaToken = request.recaptchaToken();
        User currentUser = getCurrentUser();
        log.info("Change subscription : {} to {}", currentUser.getSubscription(), subscription);

        if (StringUtils.isEmpty(recaptchaToken) || !recaptchaConfig.verifyCaptcha(recaptchaToken)) {
            throw new AuthException("ReCaptcha validation failed");
        }

        Subscription sub = Subscription.valueOf(subscription.toUpperCase());

        String invoiceResponse = payriffService.createInvoiceWithUser(paymentRequest, currentUser);

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .userId(currentUser.getId())
                .date(LocalDateTime.now())
                .amount(BigDecimal.valueOf(Long.parseLong(paymentRequest.getAmount())))
                .billUrl(invoiceResponse).paymentStatus(PaymentStatus.SUCCESS)
                .paymentType(PaymentType.CARD)
                .subscription(sub)
                .build();

        paymentHistoryService.save(paymentHistory);

        currentUser.setSubscription(sub);
        if (!Subscription.FREE.equals(sub)) {
            boolean subscriptionMonthly = paymentRequest.getAmount().equals(sub.getPriceForOneMonth().toString());
            currentUser.setSubscriptionMonthly(subscriptionMonthly);
            currentUser.setNextPaymentTime(subscriptionMonthly ? LocalDateTime.now().plusMonths(1) : LocalDateTime.now().plusYears(1));
        } else {
            currentUser.setSubscriptionMonthly(false);
            currentUser.setNextPaymentTime(null);
        }
        userRepository.save(currentUser);
        return invoiceResponse;
    }


    @Override
    public ProfileSecurityResponse updateSecurity(ProfileSecurityRequest request) throws AuthException {
        log.info("Update security : {}", request);
        User currentUser = getCurrentUser();
        if (!StringUtil.isBlank(request.oldPassword()) &&
                passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
            currentUser.setUpdatedAt(LocalDateTime.now());
        }
        if (!StringUtil.isBlank(request.recoveryEmail())) {
            if (currentUser.getEmail().equals(request.recoveryEmail())) {
                String errorMessage = messageSource.getMessage("recovery-email.error", null,
                        new Locale(currentUser.getLanguage().name().toLowerCase()));
                log.warn(errorMessage);
                throw new RuntimeException(errorMessage + " (email: " + request.recoveryEmail() + " )");
            }
            currentUser.setRecoveryEmail(request.recoveryEmail());
        }
        if (!StringUtil.isBlank(request.recoveryPhoneNumber())) {
            if (currentUser.getPhoneNumber().equals(request.recoveryPhoneNumber())) {
                String errorMessage = messageSource.getMessage("recovery-phone-number.error", null,
                        new Locale(currentUser.getLanguage().name().toLowerCase()));
                log.warn(errorMessage);
                throw new RuntimeException(errorMessage + " (phone number: " + request.recoveryPhoneNumber() + " )");
            }
            currentUser.setRecoveryPhoneNumber(request.recoveryPhoneNumber());
        }
        User user = userRepository.save(currentUser);
        return new ProfileSecurityResponse(
                user.getRecoveryEmail(),
                user.getRecoveryPhoneNumber());
    }

    private UserResponseForAdmin activateUser(User user) throws AuthException {
        user.setActive(true);
        user.setDeactivateReason(null);
        user.setDeactivateTime(null);
        return getUserResponseForAdminTranslated(userRepository.save(user), getCurrentUser());
    }

    private UserResponseForAdmin deactivateUser(String reason, User user) throws AuthException {
        user.setActive(false);
        User admin = getCurrentUser();
        user.setDeactivateReason(translateService.translate(
                admin.getLanguage().name().toLowerCase(),
                user.getLanguage().name().toLowerCase(),
                reason));
        user.setDeactivateTime(LocalDateTime.now());
        return getUserResponseForAdminTranslated(userRepository.save(user), admin);
    }

    private Map<Subscription, SubscriptionDataDto> getSubscriptions(User currentUser) {
        Map<Subscription, SubscriptionDataDto> subscriptions = new HashMap<>();

        for (Subscription sub : Subscription.values()) {
            List<String> subscriptionDetails = Arrays.stream(messageSource.getMessage(
                            sub.name().toLowerCase() + "-pack",
                            null,
                            new Locale(currentUser.getLanguage().name().toLowerCase())).split("\\."))
                    .map(String::trim).toList();

            subscriptions.put(sub,
                    new SubscriptionDataDto(sub.getPriceForOneMonth(),
                            sub.getPriceForOneYear(),
                            subscriptionDetails
                    ));
        }
        return subscriptions;
    }

    private UserResponseForAdmin getUserResponseForAdminTranslated(User user, User admin) {
        if (!user.getLanguage().equals(admin.getLanguage())) {
            String userLanguage = user.getLanguage().name().toLowerCase();
            String adminLanguage = admin.getLanguage().name().toLowerCase();
            if (!user.isActive())
                user.setDeactivateReason(translateService.translate(
                        userLanguage,
                        adminLanguage,
                        user.getDeactivateReason()));
            if (user.getPosition() != null)
                user.setPosition(translateService.translate(
                        userLanguage,
                        adminLanguage,
                        user.getPosition()));
        }
        return userToUserResponseForAdmin(user);
    }
}
