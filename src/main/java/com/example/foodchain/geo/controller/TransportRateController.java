package com.example.foodchain.geo.controller;

import com.example.foodchain.geo.dto.TransportRateRequest;
import com.example.foodchain.geo.dto.TransportRateResponse;
import com.example.foodchain.geo.service.TransportRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/transport-rates")
@Tag(name = "Géolocalisation - Tarifs transport (ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class TransportRateController {

    private final TransportRateService service;

    public TransportRateController(TransportRateService service) {
        this.service = service;
    }

    @Operation(summary = "Lister les tarifs de transport")
    @GetMapping
    public List<TransportRateResponse> list() {
        return service.list();
    }

    @Operation(summary = "Créer un tarif de transport")
    @PostMapping
    public ResponseEntity<TransportRateResponse> create(@Valid @RequestBody TransportRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Mettre à jour un tarif de transport")
    @PutMapping("/{id}")
    public TransportRateResponse update(@PathVariable UUID id, @Valid @RequestBody TransportRateRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Supprimer un tarif de transport")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
