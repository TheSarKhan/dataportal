package org.example.dataprotal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public record ProfileUpdateRequest(@Email String email,
                                   @Min(7) String phoneNumber,
                                   String workplace,
                                   String position) {
}
