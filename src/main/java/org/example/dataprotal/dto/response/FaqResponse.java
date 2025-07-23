package org.example.dataprotal.dto.response;

import java.util.Map;

public record FaqResponse(String header,
                          Map<String, Map<String, String>> headersSubHeadersAndTheirContentMap) {
}
