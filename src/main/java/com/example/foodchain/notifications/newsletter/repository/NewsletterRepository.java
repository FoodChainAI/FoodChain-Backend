package com.example.foodchain.notifications.newsletter.repository;

import com.example.foodchain.notifications.newsletter.entity.NewsletterSubscriber;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscriber, UUID> {
    boolean existsByEmail(String email);
}
