package com.example.foodchain.catalog.mapper;

import com.example.foodchain.catalog.dto.OfferResponse;
import com.example.foodchain.catalog.dto.OfferSnapshot;
import com.example.foodchain.catalog.dto.ProductResponse;
import com.example.foodchain.catalog.entity.Offer;
import com.example.foodchain.catalog.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getUnit(),
                product.getBasePrice());
    }

    public OfferResponse toOfferResponse(Offer offer, boolean sellerVerified) {
        return new OfferResponse(
                offer.getId(),
                offer.getSellerId(),
                sellerVerified,
                toProductResponse(offer.getProduct()),
                offer.getQuantity(),
                offer.getPrice(),
                offer.isAvailable(),
                offer.getLocation(),
                offer.getCreatedAt());
    }

    public OfferSnapshot toSnapshot(Offer offer) {
        return new OfferSnapshot(
                offer.getId(),
                offer.getProduct().getId(),
                offer.getSellerId(),
                offer.getPrice(),
                offer.getQuantity(),
                offer.isAvailable());
    }
}
