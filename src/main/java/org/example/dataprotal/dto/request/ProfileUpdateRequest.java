package org.example.dataprotal.dto.request;

public record ProfileUpdateRequest(String email,
                                   String phoneNumber,
                                   String workplace,
                                   String position) {
}
