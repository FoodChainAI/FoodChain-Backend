package com.example.foodchain.trust.service;

import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.orders.service.OrderService;
import com.example.foodchain.trust.dto.CreateReviewRequest;
import com.example.foodchain.trust.dto.ReviewResponse;
import com.example.foodchain.trust.entity.Review;
import com.example.foodchain.trust.repository.ReviewRepository;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    public ReviewService(ReviewRepository reviewRepository, OrderService orderService) {
        this.reviewRepository = reviewRepository;
        this.orderService = orderService;
    }

    @Transactional
    public ReviewResponse create(UUID authorId, CreateReviewRequest request) {
        orderService.requireDeliveredOrder(request.orderId(), authorId);
        if (reviewRepository.existsByOrderIdAndAuthorId(request.orderId(), authorId)) {
            throw new ConflictException("REVIEW_EXISTS",
                    "Vous avez déjà noté cette commande.",
                    Map.of("orderId", request.orderId().toString()));
        }
        Review review = Review.create(request.orderId(), authorId, request.rating(), request.comment());
        review = reviewRepository.save(review);
        return toResponse(review);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getOrderId(),
                review.getAuthorId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }
}
