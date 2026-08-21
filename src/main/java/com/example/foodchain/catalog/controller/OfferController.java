package com.example.foodchain.catalog.controller;

import com.example.foodchain.catalog.dto.CreateOfferRequest;
import com.example.foodchain.catalog.dto.OfferResponse;
import com.example.foodchain.catalog.dto.UpdateStockRequest;
import com.example.foodchain.catalog.service.OfferService;
import com.example.foodchain.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/offers")
@Tag(name = "Catalogue - Offres")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @Operation(summary = "Rechercher des offres par catégorie, localisation et disponibilité (public)")
    @GetMapping
    public List<OfferResponse> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean available) {
        return offerService.search(category, location, available);
    }

    @Operation(summary = "Mes offres (vendeur authentifié)")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @GetMapping("/mine")
    public List<OfferResponse> mine() {
        return offerService.findBySeller(SecurityUtils.currentUserId());
    }

    @Operation(summary = "Détail d'une offre (public)")
    @GetMapping("/{id}")
    public OfferResponse get(@PathVariable UUID id) {
        return offerService.getById(id);
    }

    @Operation(summary = "Publier une offre / déclarer une récolte (vendeur)")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody CreateOfferRequest request) {
        OfferResponse created = offerService.create(SecurityUtils.currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Mettre à jour le stock d'une offre (vendeur propriétaire)")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @PatchMapping("/{id}/stock")
    public OfferResponse updateStock(@PathVariable UUID id, @Valid @RequestBody UpdateStockRequest request) {
        return offerService.updateStock(SecurityUtils.currentUserId(), id, request.quantity());
    }
}
