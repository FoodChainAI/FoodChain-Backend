package com.example.foodchain.notifications.email;

/**
 * Transactional email sender. Implementations must never throw to callers —
 * a mail failure must not break the business operation that triggered it.
 */
public interface EmailService {

    /** Low-level send of an HTML email. */
    void send(String to, String subject, String html);

    /** Welcome email sent right after a successful registration. */
    void sendWelcome(String to, String role);

    /** Confirmation email sent after a newsletter subscription. */
    void sendNewsletterWelcome(String to);
}
