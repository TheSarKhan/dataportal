package org.example.dataprotal.dto.response;

import org.example.dataprotal.enums.Language;
import org.example.dataprotal.model.user.Subscription;

import java.util.List;
import java.util.Map;

public record ProfileSettingsResponse(Language currentLanguage,
                                      Map<Language, String> languages,
                                      Long currentSubscriptionId,
                                      List<Subscription> subscriptions,
                                      List<String> defaultDeactivateReasons) {
}
