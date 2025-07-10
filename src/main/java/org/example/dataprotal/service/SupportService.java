package org.example.dataprotal.service;

import org.example.dataprotal.dto.response.FaqResponse;
import org.example.dataprotal.dto.response.UserInstructionResponse;

import java.util.List;

public interface SupportService {
    List<String> getCategories();

    FaqResponse getFagInfo();

    UserInstructionResponse getUserInstruction();
}
