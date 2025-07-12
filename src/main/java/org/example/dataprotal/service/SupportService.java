package org.example.dataprotal.service;

import org.example.dataprotal.dto.request.ContactFormRequest;
import org.example.dataprotal.dto.response.ContactFormResponse;
import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;

import java.util.List;

public interface SupportService {
    List<String> getCategories(String language);

    FaqResponse getFagInfo(String language);

    UserInstructionResponse getUserInstruction(String language);

    ContactFormResponse getContactForm(String language);

    String sendContactForm(ContactFormRequest request);
}
