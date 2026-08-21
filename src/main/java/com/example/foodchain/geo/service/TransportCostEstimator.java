package com.example.foodchain.geo.service;

import com.example.foodchain.geo.dto.TransportEstimate;
import com.example.foodchain.geo.entity.TransportRate;
import com.example.foodchain.geo.repository.TransportRateRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Estimates transport cost between two localities.
 *
 * <p>Distance = centroid-to-centroid great-circle distance, multiplied by a
 * configurable road-sinuosity factor (default 1.3) because straight-line
 * distance systematically underestimates real road distance. The result is
 * ALWAYS an estimate.
 */
@Service
public class TransportCostEstimator {

    private final LocalityService localityService;
    private final TransportRateRepository rateRepository;
    private final double sinuosityFactor;

    public TransportCostEstimator(LocalityService localityService,
                                  TransportRateRepository rateRepository,
                                  @Value("${foodchain.geo.sinuosity-factor:1.3}") double sinuosityFactor) {
        this.localityService = localityService;
        this.rateRepository = rateRepository;
        this.sinuosityFactor = sinuosityFactor;
    }

    /**
     * @param weightTons load weight in metric tons (use a small default for retail-size orders)
     */
    @Transactional(readOnly = true)
    public TransportEstimate estimate(UUID fromLocalityId, UUID toLocalityId, BigDecimal weightTons) {
        double roadKm = localityService.distanceKm(fromLocalityId, toLocalityId) * sinuosityFactor;
        BigDecimal distanceKm = BigDecimal.valueOf(roadKm).setScale(1, RoundingMode.HALF_UP);

        TransportRate rate = rateRepository.findByActiveTrueOrderByValidFromDesc().stream()
                .findFirst()
                .orElse(null);
        if (rate == null) {
            return new TransportEstimate(distanceKm, null, null, true,
                    "Aucun tarif de transport actif — coût indisponible.");
        }

        BigDecimal tons = weightTons == null || weightTons.signum() <= 0 ? BigDecimal.valueOf(0.05) : weightTons;
        BigDecimal raw = rate.getCostPerKmPerTon()
                .multiply(distanceKm)
                .multiply(tons);
        BigDecimal cost = raw.max(rate.getMinCost()).setScale(0, RoundingMode.HALF_UP);
        return TransportEstimate.of(distanceKm, cost, rate.getVehicleClass());
    }
}
