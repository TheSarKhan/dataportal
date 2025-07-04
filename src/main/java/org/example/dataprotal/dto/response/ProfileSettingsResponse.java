package org.example.dataprotal.dto.response;

import org.example.dataprotal.dto.SubscriptionDataDto;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.enums.Subscription;

import java.util.List;
import java.util.Map;

public record ProfileSettingsResponse(Language currentLanguage, Map<Language, String> languages,
                                      Subscription currentSubscription, Map<Subscription, SubscriptionDataDto> subscriptions,
                                      List<String> defaultDeactivateReasons) {
}
