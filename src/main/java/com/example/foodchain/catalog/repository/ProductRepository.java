package com.example.foodchain.catalog.repository;

import com.example.foodchain.catalog.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
