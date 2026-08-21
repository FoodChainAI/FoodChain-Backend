package com.example.foodchain.catalog.repository;

import com.example.foodchain.catalog.entity.Offer;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    /**
     * Loads an offer with a pessimistic write lock (SELECT ... FOR UPDATE).
     * Used during payment settlement to serialise concurrent stock decrements
     * and prevent overselling.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Offer o where o.id = :id")
    Optional<Offer> lockById(@Param("id") UUID id);

    List<Offer> findBySellerId(UUID sellerId);

    /**
     * Filtered search. Any filter left null is ignored.
     */
    @Query("""
            select o from Offer o
            where (:category is null or lower(o.product.category) = lower(:category))
              and (:location is null or lower(o.location) like lower(concat('%', :location, '%')))
              and (:available is null or o.available = :available)
            order by o.createdAt desc
            """)
    List<Offer> search(@Param("category") String category,
                       @Param("location") String location,
                       @Param("available") Boolean available);
}
