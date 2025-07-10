package org.example.dataprotal.service.impl;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.response.ContactFormResponse;
import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.service.SupportService;
import org.example.dataprotal.service.TranslateService;
import org.example.dataprotal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
    private final MessageSource messageSource;

    private final UserService userService;

    private final TranslateService translateService;

    @Value("${fag.headers}")
    String headerCount;

    @Value("${fag.subheaders}")
    String subheaderCount;

    @Override
    public List<String> getCategories() {
        log.info("Get support categories");
        Locale locale = getLocale();

        return Arrays.stream(messageSource.getMessage(
                                "support.categories", null, locale)
                        .split("\\."))
                .toList();
    }

    @Override
    public FaqResponse getFagInfo() {
        log.info("Get support headers and subheaders");

        Locale locale = getLocale();

        Map<String, Map<String, String>> headersSubHeadersAndTheirContentMap = new HashMap<>();
        int headers = Integer.parseInt(headerCount);

        String subCount = subheaderCount;
        int[] subheaders = new int[headers];
        String[] subheaderCount = subCount.split("\\.");
        for (int i = 0; i < headers; i++) {
            subheaders[i] = Integer.parseInt(subheaderCount[i]);
        }

        for (int i = 0; i < headers; i++) {
            Map<String, String> subheadersAndTheirContentMap = new HashMap<>();
            for (int j = 0; j < subheaders[i]; j++) {
                String subheader = messageSource.getMessage("faq.subheaders." + (i + 1) + "." + (j + 1), null, locale);
                String subheaderContent = messageSource.getMessage("faq.content." + (i + 1) + "." + (j + 1), null, locale);
                subheadersAndTheirContentMap.put(subheader, subheaderContent);
            }
            String header = messageSource.getMessage("faq.headers." + (i + 1), null, locale);
            headersSubHeadersAndTheirContentMap.put(header, subheadersAndTheirContentMap);
        }

        String faqHeader = messageSource.getMessage("support.categories.fag", null, locale);
        return new FaqResponse(faqHeader, headersSubHeadersAndTheirContentMap);
    }

    @Override
    public UserInstructionResponse getUserInstruction() {
        log.info("Get user instruction");
        Locale locale = getLocale();
        String contactFormHeader = messageSource.getMessage("support.categories.ui", null, locale);
        String translated = translateService.translate(
                "en",
                locale.getLanguage(),
                "User instruction is not available in this version.");
        return new UserInstructionResponse(contactFormHeader, translated);
    }

    private Locale getLocale() {
        Locale locale = new Locale("en");
        try {
            User currentUser = userService.getCurrentUser();
            locale = new Locale(currentUser.getLanguage().name().toLowerCase());
        } catch (AuthException ignored) {
        }
        return locale;
    }
}
