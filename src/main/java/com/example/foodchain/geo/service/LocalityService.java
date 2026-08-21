package com.example.foodchain.geo.service;

import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.geo.dto.LocalityResponse;
import com.example.foodchain.geo.entity.Locality;
import com.example.foodchain.geo.entity.LocalityLevel;
import com.example.foodchain.geo.repository.LocalityRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Public entry point of the geo module for locality lookups. */
@Service
public class LocalityService {

    private final LocalityRepository repository;

    public LocalityService(LocalityRepository repository) {
        this.repository = repository;
    }

    /** Cascade selector feed: children of {@code parentId}, optionally filtered by level. */
    @Transactional(readOnly = true)
    public List<LocalityResponse> list(UUID parentId, LocalityLevel level) {
        List<Locality> localities;
        if (parentId != null && level != null) {
            localities = repository.findByParentIdAndLevelOrderByName(parentId, level);
        } else if (parentId != null) {
            localities = repository.findByParentIdOrderByName(parentId);
        } else if (level != null) {
            localities = repository.findByLevelOrderByName(level);
        } else {
            // No filter → top-level regions.
            localities = repository.findByParentIdIsNullOrderByName();
        }
        return localities.stream().map(LocalityService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LocalityResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return repository.searchByName(query.trim()).stream().map(LocalityService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocalityResponse nearest(double latitude, double longitude) {
        return repository.findNearest(latitude, longitude)
                .map(LocalityService::toResponse)
                .orElseThrow(() -> new NotFoundException("Aucune localité trouvée."));
    }

    @Transactional(readOnly = true)
    public Locality getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Localité introuvable."));
    }

    /** Distance in kilometers between two localities' centroids. */
    @Transactional(readOnly = true)
    public double distanceKm(UUID fromLocalityId, UUID toLocalityId) {
        Double meters = repository.distanceMeters(fromLocalityId, toLocalityId);
        return meters == null ? 0d : meters / 1000d;
    }

    static LocalityResponse toResponse(Locality l) {
        return new LocalityResponse(
                l.getId(), l.getName(), l.getLevel(), l.getParentId(),
                l.getLatitude(), l.getLongitude(), l.isApproximate());
    }
}
