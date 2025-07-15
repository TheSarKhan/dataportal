package org.example.dataprotal.dto.request;

public record ChangeSubscriptionRequest(Long subscriptionId,
                                        String currency,
                                        String recaptchaToken) {
}
