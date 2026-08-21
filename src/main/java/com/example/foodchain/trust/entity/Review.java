package com.example.foodchain.trust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Review() {
    }

    private Review(UUID id, UUID orderId, UUID authorId, int rating, String comment, OffsetDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.authorId = authorId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static Review create(UUID orderId, UUID authorId, int rating, String comment) {
        return new Review(UUID.randomUUID(), orderId, authorId, rating, comment, OffsetDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public UUID getAuthorId() { return authorId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
