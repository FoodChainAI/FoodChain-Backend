package com.example.foodchain.notifications.email;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Sends email through the Resend HTTP API (https://resend.com).
 *
 * <p>Emails are dispatched asynchronously and defensively: if the API key is
 * missing/disabled or the call fails, we log and move on — registration and
 * newsletter subscriptions never fail because of email.
 */
@Service
public class ResendEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String from;
    private final boolean enabled;

    public ResendEmailService(
            @Value("${foodchain.mail.resend.api-key:}") String apiKey,
            @Value("${foodchain.mail.from:FoodChain AI <onboarding@resend.dev>}") String from,
            @Value("${foodchain.mail.enabled:true}") boolean enabled,
            @Value("${foodchain.mail.resend.base-url:https://api.resend.com}") String baseUrl) {
        this.apiKey = apiKey;
        this.from = from;
        this.enabled = enabled;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Async
    @Override
    public void send(String to, String subject, String html) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            log.info("[MAIL disabled] would send '{}' to {}", subject, to);
            return;
        }
        try {
            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", from,
                            "to", new String[]{to},
                            "subject", subject,
                            "html", html))
                    .retrieve()
                    .toBodilessEntity();
            log.info("[MAIL] sent '{}' to {}", subject, to);
        } catch (Exception ex) {
            log.error("[MAIL] failed to send '{}' to {}: {}", subject, to, ex.getMessage());
        }
    }

    @Async
    @Override
    public void sendWelcome(String to, String role) {
        send(to, "Bienvenue sur FoodChain AI 🌱", EmailTemplates.welcome(to, role));
    }

    @Async
    @Override
    public void sendNewsletterWelcome(String to) {
        send(to, "Vous êtes inscrit à la newsletter FoodChain AI", EmailTemplates.newsletter(to));
    }
}
