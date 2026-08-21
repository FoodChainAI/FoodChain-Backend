package com.example.foodchain.notifications.newsletter.service;

import com.example.foodchain.notifications.email.EmailService;
import com.example.foodchain.notifications.newsletter.entity.NewsletterSubscriber;
import com.example.foodchain.notifications.newsletter.repository.NewsletterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterService {

    private final NewsletterRepository repository;
    private final EmailService emailService;

    public NewsletterService(NewsletterRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    /**
     * Idempotent subscription: registering an already-subscribed email is a no-op
     * (still returns success so we don't leak who is subscribed).
     */
    @Transactional
    public void subscribe(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            return;
        }
        repository.save(NewsletterSubscriber.create(email));
        emailService.sendNewsletterWelcome(email);
    }
}
