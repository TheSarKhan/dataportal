package org.example.dataprotal.dto.response;

public record ProfileResponse(Long id,
                              String profileImage,
                              String firstName,
                              String lastName,
                              String email,
                              String phoneNumber,
                              String workplace,
                              String position) {
}
