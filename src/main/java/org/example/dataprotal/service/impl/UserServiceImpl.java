package org.example.dataprotal.service.impl;

import com.cloudinary.utils.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.example.dataprotal.config.RecaptchaConfig;
import org.example.dataprotal.dto.request.ChangeSubscriptionRequest;
import org.example.dataprotal.dto.request.ProfileSecurityRequest;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.dto.response.UserResponseForAdmin;
import org.example.dataprotal.email.service.EmailService;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.enums.PaymentStatus;
import org.example.dataprotal.enums.PaymentType;
import org.example.dataprotal.enums.Role;
import org.example.dataprotal.exception.InvoiceCanNotBeCreatedException;
import org.example.dataprotal.mapper.UserMapper;
import org.example.dataprotal.model.user.PaymentHistory;
import org.example.dataprotal.model.user.Subscription;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.dto.PayriffInvoiceRequest;
import org.example.dataprotal.payment.service.PayriffService;
import org.example.dataprotal.redis.RedisService;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.service.CloudinaryService;
import org.example.dataprotal.service.PaymentHistoryService;
import org.example.dataprotal.service.SubscriptionService;
import org.example.dataprotal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
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

    private final ReloadableResourceBundleMessageSource messageSource;

    private final PayriffService payriffService;

    private final PaymentHistoryService paymentHistoryService;

    private final SubscriptionService subscriptionService;

    private final RecaptchaConfig recaptchaConfig;

    private final RedisService redisService;

    private final EmailService emailService;

    @Value("${spring.application.base-url}")
    private String baseUrl;

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
        User currentUser = getCurrentUser();
        log.info("Get current profile : {}", currentUser.getFirstName());
        return userToProfileResponse(currentUser);
    }

    @Override
    public List<UserResponseForAdmin> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAll().stream()
                .map(UserMapper::userToUserResponseForAdmin)
                .toList();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id : {}", id);
            return new NoSuchElementException("User not found with id : " + id);
        });
    }

    @Override
    public UserResponseForAdmin getUserById(Long id) {
        log.info("Get user by id : {}", id);
        return userToUserResponseForAdmin(getById(id));
    }

    @Override
    public UserResponseForAdmin getUserByEmail(String email) {
        log.info("Get user by email : {}", email);
        return userToUserResponseForAdmin(getByEmail(email));
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
                currentUser.getSubscriptionId(),
                subscriptionService.getAllSubscriptions(),
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
    public List<UserResponseForAdmin> searchUserByName(String name) {
        return userRepository.searchUserByName(name).stream()
                .map(UserMapper::userToUserResponseForAdmin)
                .toList();
    }

    @Override
    @Transactional
    public String  updateProfile(ProfileUpdateRequest request,
                                         MultipartFile profileImage) throws AuthException, IOException, MessagingException {
        log.info("Update profile : {}", request);
        User currentUser = getCurrentUser();
        log.info("Current user : {}", currentUser.getEmail());
        String oldProfileImageUrl = currentUser.getProfileImage();
        String url = cloudinaryService.uploadFile(profileImage, "4sim-profileImage");
        currentUser.setProfileImage(url);
        currentUser.setPhoneNumber(request.phoneNumber());
        currentUser.setWorkplace(request.workplace());
        currentUser.setPosition(request.position());
        currentUser.setUpdatedAt(LocalDateTime.now());
        if (!currentUser.getEmail().equals(request.email())){
            currentUser.setEmail(request.email());
            currentUser.setVerified(false);
            String verificationToken = UUID.randomUUID().toString();
            log.info("TOKEN {}", verificationToken);
            redisService.saveVerificationTokenToRedis(request.email(), verificationToken, 10);
            String verificationUrl = baseUrl + "/api/v1/auth/verify?token=" + verificationToken;
            emailService.sendVerificationEmail(request.email(), verificationUrl);
            log.info("Verification email sent to {}", request.email());
            userRepository.save(currentUser);
            cloudinaryService.deleteFile(oldProfileImageUrl);
            return "Check your email to verify account";
        }
        userRepository.save(currentUser);
        cloudinaryService.deleteFile(oldProfileImageUrl);
        return "User updated successfully.";
    }

    @Override
    public ProfileResponse updateLanguage(String language) throws AuthException {
        log.info("Update language : {}", language);
        User user = getCurrentUser();
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
    public UserResponseForAdmin deactivateUserWithId(Long id, String reason) {
        log.info("Deactivate user with id : {}", id);
        User user = getById(id);
        return deactivateUser(reason, user);
    }

    @Override
    public UserResponseForAdmin deactivateUserWithEmail(String email, String reason) {
        log.info("Deactivate user with email : {}", email);
        User user = getByEmail(email);
        return deactivateUser(reason, user);
    }

    @Override
    public UserResponseForAdmin activateUserWithId(Long id) {
        log.info("Activate user with id : {}", id);
        User user = getById(id);
        return activateUser(user);
    }

    @Override
    public UserResponseForAdmin activateUserWithEmail(String email) {
        log.info("Activate user with email : {}", email);
        User user = getByEmail(email);
        return activateUser(user);
    }

    @Override
    public UserResponseForAdmin changeUserRole(Long id, String role) {
        log.info("Change user role with id : {} to {}", id, role);
        User user = getById(id);
        user.setRole(Role.valueOf(role.toUpperCase()));
        return userToUserResponseForAdmin(userRepository.save(user));
    }

    @Override
    @Transactional
    public String changeSubscription(ChangeSubscriptionRequest request, PayriffInvoiceRequest paymentRequest) throws AuthException, InvoiceCanNotBeCreatedException {
        Subscription subscription = subscriptionService.getSubscriptionById(request.subscriptionId());
        String recaptchaToken = request.recaptchaToken();
        User currentUser = getCurrentUser();
        log.info("Change subscription : {} to {}", currentUser.getSubscriptionId(), subscription);

        if (StringUtils.isEmpty(recaptchaToken) || !recaptchaConfig.verifyCaptcha(recaptchaToken)) {
            throw new AuthException("ReCaptcha validation failed");
        }

        String invoiceResponse = payriffService.createInvoiceWithUser(paymentRequest, currentUser);

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .userId(currentUser.getId())
                .date(LocalDateTime.now())
                .amount(BigDecimal.valueOf(Long.parseLong(paymentRequest.getAmount())))
                .billUrl(invoiceResponse).paymentStatus(PaymentStatus.SUCCESS)
                .paymentType(PaymentType.CARD)
                .subscriptionId(subscription.getId())
                .build();

        paymentHistoryService.save(paymentHistory);

        currentUser.setSubscriptionId(subscription.getId());
        if (!subscription.getName().equals("FREE")) {
            boolean isSubscriptionMonthly = paymentRequest.getAmount().equals(
                    subscription.getCureencyMonthlyAndYearlyPriceMap()
                            .get(request.currency()).get(0).toString());

            currentUser.setSubscriptionMonthly(isSubscriptionMonthly);
            currentUser.setNextPaymentTime(isSubscriptionMonthly ? LocalDateTime.now().plusMonths(1) : LocalDateTime.now().plusYears(1));
        } else {
            currentUser.setSubscriptionMonthly(false);
            currentUser.setNextPaymentTime(null);
        }
        userRepository.save(currentUser);
        return invoiceResponse;
    }


    @Override
    @Transactional
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

    private UserResponseForAdmin activateUser(User user) {
        user.setActive(true);
        user.setDeactivateReason(null);
        user.setDeactivateTime(null);
        return userToUserResponseForAdmin(userRepository.save(user));
    }

    private UserResponseForAdmin deactivateUser(String reason, User user) {
        user.setActive(false);
        user.setDeactivateReason(reason);
        user.setDeactivateTime(LocalDateTime.now());
        return userToUserResponseForAdmin(userRepository.save(user));
    }
}
