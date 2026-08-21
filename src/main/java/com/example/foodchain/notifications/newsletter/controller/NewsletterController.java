package com.example.foodchain.notifications.newsletter.controller;

import com.example.foodchain.notifications.newsletter.dto.SubscribeRequest;
import com.example.foodchain.notifications.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/newsletter")
@Tag(name = "Newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @Operation(summary = "S'inscrire à la newsletter (public) et recevoir un email de confirmation")
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@Valid @RequestBody SubscribeRequest request) {
        newsletterService.subscribe(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("message", "Inscription enregistrée. Vérifiez votre boîte mail."));
    }
}
