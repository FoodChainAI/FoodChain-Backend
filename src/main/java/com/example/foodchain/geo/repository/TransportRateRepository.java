package com.example.foodchain.geo.repository;

import com.example.foodchain.geo.entity.TransportRate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRateRepository extends JpaRepository<TransportRate, UUID> {

    List<TransportRate> findByActiveTrueOrderByValidFromDesc();

    Optional<TransportRate> findFirstByVehicleClassAndActiveTrueOrderByValidFromDesc(String vehicleClass);
}
