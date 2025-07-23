package org.example.dataprotal.profile;

import jakarta.mail.MessagingException;
import jakarta.security.auth.message.AuthException;
import org.example.dataprotal.config.RecaptchaConfig;
import org.example.dataprotal.dto.request.ProfileUpdateRequest;
import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.ProfileSecurityResponse;
import org.example.dataprotal.dto.response.ProfileSettingsResponse;
import org.example.dataprotal.dto.response.UserResponseForAdmin;
import org.example.dataprotal.email.service.EmailService;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.mapper.UserMapper;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.payment.service.PayriffService;
import org.example.dataprotal.redis.RedisService;
import org.example.dataprotal.repository.user.UserRepository;
import org.example.dataprotal.service.CloudinaryService;
import org.example.dataprotal.service.PaymentHistoryService;
import org.example.dataprotal.service.SubscriptionService;
import org.example.dataprotal.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private ReloadableResourceBundleMessageSource messageSource;

    @Mock
    private PayriffService payriffService;

    @Mock
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private RecaptchaConfig recaptchaConfig;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private static User user;

    private static UserResponseForAdmin userResponseForAdmin;

    private static ProfileResponse profileResponse;

    private static String nameForSearch;

    private static ProfileUpdateRequest updateRequest;

    private static MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("a@gmail.com")
                .language(Language.EN)
                .subscriptionId(1L)
                .phoneNumber("+994 (70) 987 65 43")
                .recoveryEmail("recoveryemail@gmail.com")
                .recoveryPhoneNumber("+994 (70) 123 45 67")
                .build();

        updateRequest = new ProfileUpdateRequest(
                "new@gmail.com",
                "+994 (55) 987 65 43",
                "United Payment",
                "Backend Developer");

        nameForSearch = "Johm";

        multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Spring Framework".getBytes());

        userResponseForAdmin =
                UserMapper.userToUserResponseForAdmin(user);

        profileResponse =
                UserMapper.userToProfileResponse(user);
    }


    @Test
    public void getByEmailSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User resposeUser = userService.getByEmail(user.getEmail());

        assertEquals(user, resposeUser);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getByEmailFail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException =
                assertThrows(NoSuchElementException.class, () -> userService.getByEmail(user.getEmail()));

        assertEquals("User not found with email : " + user.getEmail(),
                noSuchElementException.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getCurrentUserSuccess() throws AuthException {
        securityContextHolderConfigCorrectly();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);

        verify(SecurityContextHolder.getContext(), times(1)).getAuthentication();
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getCurrentUserFail() {
        securityContextHolderConfigWrong();

        AuthException authException =
                assertThrows(AuthException.class, () -> userService.getCurrentUser());

        assertEquals("User doesn't login!!!", authException.getMessage());

        verify(SecurityContextHolder.getContext(), times(1)).getAuthentication();
    }


    @Test
    public void getCurrentProfileSuccess() throws AuthException {
        securityContextHolderConfigCorrectly();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ProfileResponse currentProfile = userService.getCurrentProfile();

        assertEquals(profileResponse, currentProfile);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getCurrentProfileFail() {
        securityContextHolderConfigWrong();

        AuthException authException =
                assertThrows(AuthException.class, () -> userService.getCurrentProfile());

        assertEquals("User doesn't login!!!", authException.getMessage());

        verify(SecurityContextHolder.getContext(), times(1)).getAuthentication();
    }

    @Test
    public void getAllUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseForAdmin> allUsers = userService.getAllUsers();

        assertEquals(1, allUsers.size());
        assertEquals(userResponseForAdmin, allUsers.get(0));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getAllUsersFail() {
        when(userRepository.findAll()).thenReturn(java.util.List.of());

        List<UserResponseForAdmin> allUsers = userService.getAllUsers();

        assertEquals(0, allUsers.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getByIdSuccess() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User retrievedUser = userService.getById(user.getId());

        assertEquals(user, retrievedUser);

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void getByIdFail() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException =
                assertThrows(NoSuchElementException.class,
                        () -> userService.getById(user.getId()));

        assertEquals("User not found with id : " + user.getId(),
                noSuchElementException.getMessage());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void getUserByIdSuccess() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponseForAdmin userById = userService.getUserById(user.getId());

        assertEquals(userResponseForAdmin, userById);

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void getUserByIdFail() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException =
                assertThrows(NoSuchElementException.class,
                        () -> userService.getUserById(user.getId()));

        assertEquals("User not found with id : " + user.getId(),
                noSuchElementException.getMessage());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void getUserByEmailSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserResponseForAdmin userByEmail = userService.getUserByEmail(user.getEmail());

        assertEquals(userResponseForAdmin, userByEmail);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getUserByEmailFail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException =
                assertThrows(NoSuchElementException.class,
                        () -> userService.getUserByEmail(user.getEmail()));

        assertEquals("User not found with email : " + user.getEmail(),
                noSuchElementException.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getProfileSettingsSuccess() throws AuthException {
        securityContextHolderConfigCorrectly();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(messageSource.getMessage("deactivate-reasons", null, new Locale("en"))).thenReturn("deactivate-reason1. deactivate-reason2.");
        when(subscriptionService.getAllSubscriptions()).thenReturn(List.of());

        ProfileSettingsResponse profileSettings = userService.getProfileSettings();

        assertEquals(user.getLanguage(), profileSettings.currentLanguage());
        assertEquals(3, profileSettings.languages().size());
        assertEquals(1L, profileSettings.currentSubscriptionId());
        assertEquals(0, profileSettings.subscriptions().size());
        assertEquals(List.of("deactivate-reason1", "deactivate-reason2"),
                profileSettings.defaultDeactivateReasons());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(messageSource, times(1)).getMessage("deactivate-reasons", null, new Locale("en"));
        verify(subscriptionService, times(1)).getAllSubscriptions();
    }

    @Test
    public void getProfileSettingsFail() {
        securityContextHolderConfigWrong();

        AuthException authException =
                assertThrows(AuthException.class, () -> userService.getProfileSettings());

        assertEquals("User doesn't login!!!", authException.getMessage());
    }

    @Test
    public void getProfileSecuritySuccess() throws AuthException {
        securityContextHolderConfigCorrectly();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ProfileSecurityResponse profileSecurity =
                userService.getProfileSecurity();

        assertEquals(profileSecurity.recoveryEmail(), user.getRecoveryEmail());
        assertEquals(profileSecurity.recoveryPhoneNumber(), user.getRecoveryPhoneNumber());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void getProfileSecurityFail() {
        securityContextHolderConfigWrong();

        AuthException authException =
                assertThrows(AuthException.class, () -> userService.getProfileSecurity());

        assertEquals("User doesn't login!!!", authException.getMessage());
    }

    @Test
    public void searchUserByNameSuccess() {
        when(userRepository.searchUserByName(nameForSearch)).thenReturn(List.of(user));

        List<UserResponseForAdmin> searchUserByName = userService.searchUserByName(nameForSearch);

        assertEquals(1, searchUserByName.size());
        assertEquals(userResponseForAdmin, searchUserByName.get(0));

        verify(userRepository, times(1)).searchUserByName(nameForSearch);
    }

    @Test
    public void searchUserByNameFail() {
        when(userRepository.searchUserByName(nameForSearch)).thenReturn(java.util.List.of());

        List<UserResponseForAdmin> searchUserByName = userService.searchUserByName(nameForSearch);

        assertEquals(0, searchUserByName.size());
    }

    @Test
    public void updateProfileWithEmailSuccess() throws AuthException, IOException, MessagingException {
        securityContextHolderConfigCorrectly();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cloudinaryService.uploadFile(multipartFile, "4sim-profileImage")).thenReturn("this-is-image-url");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.updateProfile(updateRequest, multipartFile);

        assertEquals("Check your email to verify account", result);

        verify(userRepository, times(1)).findByEmail("a@gmail.com");
        verify(cloudinaryService, times(1)).uploadFile(multipartFile, "4sim-profileImage");
        verify(userRepository, times(1)).save(any(User.class));
        verify(redisService, times(1)).saveVerificationTokenToRedis(eq("new@gmail.com"), any(), eq(10));
        verify(emailService, times(1)).sendVerificationEmail(eq(updateRequest.email()), any(String.class));
    }

    private static void securityContextHolderConfigCorrectly() {
        String userEmail = "a@gmail.com";
        securityContextHolderConfig(userEmail);
    }

    private static void securityContextHolderConfigWrong() {
        String anonymousUser = "anonymousUser";
        securityContextHolderConfig(anonymousUser);
    }

    private static void securityContextHolderConfig(String userEmail) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEmail);

        SecurityContextHolder.setContext(securityContext);
    }
}
