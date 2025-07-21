package org.example.dataprotal.dto.request;

public record ContactFormRequest(String name,
                                 String surname,
                                 String fatherName,
                                 String phoneNumber,
                                 String email,
                                 String applicationType,
                                 String message,
                                 String language) {
}
