package com.example.foodchain.catalog.repository;

import com.example.foodchain.catalog.entity.PriceHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
}
