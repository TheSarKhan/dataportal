package org.example.dataprotal.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.response.TokenResponse;
import org.example.dataprotal.jwt.JwtService;
import org.example.dataprotal.enums.Roles;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String imageUrl = oAuth2User.getAttribute("picture");
        String googleId = oAuth2User.getAttribute("sub");

        TokenResponse tokenResponse = registerOrLoginUser(email, firstName, lastName, googleId, imageUrl);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("accessToken", tokenResponse.getAccessToken());
        attributes.put("refreshToken", tokenResponse.getRefreshToken());

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "email");
    }

    private TokenResponse registerOrLoginUser(String email, String firstName, String lastName, String googleId, String imageUrl) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(Optional.ofNullable(firstName).orElse("Unknown"));
            newUser.setLastName(Optional.ofNullable(lastName).orElse("Unknown"));
            newUser.setGoogleId(googleId);
            newUser.setProfileImage(imageUrl);
            Set<Roles> roles = new HashSet<>();
            roles.add(Roles.USER);
            newUser.setRoles(roles);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setPassword(passwordEncoder.encode("default_password"));
            return userRepository.save(newUser);
        });

        // Update Google ID if it's a returning user
        if (!user.getGoogleId().equals(googleId)) {
            user.setGoogleId(googleId);
            userRepository.save(user);
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), null);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse processGoogleLogin(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(googleClientId)).build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String googleId = payload.getSubject();

            return registerOrLoginUser(email, firstName, lastName, googleId, pictureUrl);
        }

        throw new RuntimeException("Invalid Google ID token.");
    }
}
