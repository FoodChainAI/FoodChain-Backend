package com.example.foodchain.geo.controller;

import com.example.foodchain.geo.dto.LocalityResponse;
import com.example.foodchain.geo.entity.LocalityLevel;
import com.example.foodchain.geo.service.LocalityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/localities")
@Tag(name = "Géolocalisation - Localités")
public class LocalityController {

    private final LocalityService localityService;

    public LocalityController(LocalityService localityService) {
        this.localityService = localityService;
    }

    @Operation(summary = "Lister les localités (sélecteur en cascade) — public")
    @GetMapping
    public List<LocalityResponse> list(
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) LocalityLevel level) {
        return localityService.list(parentId, level);
    }

    @Operation(summary = "Recherche tolérante (accents/casse) — public")
    @GetMapping("/search")
    public List<LocalityResponse> search(@RequestParam String q) {
        return localityService.search(q);
    }

    @Operation(summary = "Localité la plus proche d'un point GPS (option 'utiliser ma position') — public")
    @GetMapping("/nearest")
    public LocalityResponse nearest(@RequestParam double lat, @RequestParam double lng) {
        return localityService.nearest(lat, lng);
    }
}
