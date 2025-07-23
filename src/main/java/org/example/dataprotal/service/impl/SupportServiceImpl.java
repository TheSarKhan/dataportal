package org.example.dataprotal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.request.ContactFormRequest;
import org.example.dataprotal.dto.response.ContactFormResponse;
import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;
import org.example.dataprotal.enums.ApplicationType;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.service.NotificationService;
import org.example.dataprotal.service.SupportService;
import org.example.dataprotal.service.TranslateService;
import org.example.dataprotal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
    private final ReloadableResourceBundleMessageSource messageSource;

    private final UserService userService;

    private final TranslateService translateService;

    private final NotificationService notificationService;

    @Value("${faq.headers}")
    String headerCount;

    @Value("${faq.subheaders}")
    String subheaderCount;

    @Value("${email-address-of-the-admin-supervising-the.contact-form}")
    String adminEmailAddress;

    @Override
    public List<String> getCategories(String language) {
        log.info("Get support categories");
        Locale locale = generateLocale(language);

        return List.of(messageSource.getMessage("support.categories.faq", null, locale),
                messageSource.getMessage("support.categories.ui", null, locale),
                messageSource.getMessage("support.categories.cf", null, locale));
    }

    @Override
    public FaqResponse getFagInfo(String language) {
        log.info("Get support headers and subheaders");

        Locale locale = generateLocale(language);

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
    public UserInstructionResponse getUserInstruction(String language) {
        log.info("Get user instruction");
        Locale locale = generateLocale(language);
        String userInstructionHeader = messageSource.getMessage("support.categories.ui", null, locale);
        String message = messageSource.getMessage("user-instruction.message", null, locale);
        return new UserInstructionResponse(userInstructionHeader, message);
    }

    @Override
    public ContactFormResponse getContactForm(String language) {
        log.info("Get contact form");
        Locale locale = generateLocale(language);
        String contactFormHeader = messageSource.getMessage("support.categories.cf", null, locale);
        List<String> applicationNames = Arrays.stream(ApplicationType.values())
                .map(ApplicationType::name)
                .toList();
        return new ContactFormResponse(contactFormHeader, applicationNames);
    }

    @Override
    public String sendContactForm(ContactFormRequest request) {
        log.info("Send contact form : {}", request);
        User admin = userService.getByEmail(adminEmailAddress);
        String header = request.name() +
                " " + request.surname() +
                " " + request.fatherName() +
                " (" + request.email() + ") " +
                " has submitted " +
                request.applicationType() +
                " via the contact form." + "(" +
                request.phoneNumber() + ")";
        notificationService.sendContactForm(header,
                translateForAdmin(request.language().toLowerCase(),
                        request.message(), admin),
                admin.getLanguage(),
                admin.getId());
        return messageSource.getMessage("contact-form.message.success",
                null, generateLocale(request.language()));
    }

    private static Locale generateLocale(String language) {
        Locale locale= new Locale("en");
        if (language != null &&
                (language.equalsIgnoreCase("en") ||
                        language.equalsIgnoreCase("az") ||
                        language.equalsIgnoreCase("ru")))
            locale = new Locale(language.toLowerCase());
        return locale;
    }

    private String translateForAdmin(String to, String text, User admin) {
        return translateService.translate(to,
                admin.getLanguage().name().toLowerCase(),
                text);
    }
}
