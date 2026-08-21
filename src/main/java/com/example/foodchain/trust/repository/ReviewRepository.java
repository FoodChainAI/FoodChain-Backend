package com.example.foodchain.trust.repository;

import com.example.foodchain.trust.entity.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByOrderIdAndAuthorId(UUID orderId, UUID authorId);
}
