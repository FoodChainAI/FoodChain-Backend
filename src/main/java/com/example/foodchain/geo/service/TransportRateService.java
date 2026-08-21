package com.example.foodchain.geo.service;

import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.geo.dto.TransportRateRequest;
import com.example.foodchain.geo.dto.TransportRateResponse;
import com.example.foodchain.geo.entity.TransportRate;
import com.example.foodchain.geo.repository.TransportRateRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransportRateService {

    private final TransportRateRepository repository;

    public TransportRateService(TransportRateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TransportRateResponse> list() {
        return repository.findAll().stream().map(TransportRateService::toResponse).toList();
    }

    @Transactional
    public TransportRateResponse create(TransportRateRequest req) {
        TransportRate rate = TransportRate.create(req.vehicleClass(), req.costPerKmPerTon(), req.minCost(), req.active());
        return toResponse(repository.save(rate));
    }

    @Transactional
    public TransportRateResponse update(UUID id, TransportRateRequest req) {
        TransportRate rate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarif de transport introuvable."));
        rate.setVehicleClass(req.vehicleClass());
        rate.setCostPerKmPerTon(req.costPerKmPerTon());
        rate.setMinCost(req.minCost());
        rate.setActive(req.active());
        return toResponse(repository.save(rate));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Tarif de transport introuvable.");
        }
        repository.deleteById(id);
    }

    static TransportRateResponse toResponse(TransportRate r) {
        return new TransportRateResponse(
                r.getId(), r.getVehicleClass(), r.getCostPerKmPerTon(),
                r.getMinCost(), r.isActive(), r.getValidFrom());
    }
}
