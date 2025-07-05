package org.example.dataprotal.dto.response;

import org.example.dataprotal.enums.Language;

public record ProfileResponseForHeader(Language language,
                                       String profileImage) {
}
