package com.example.foodchain.catalog.service;

import com.example.foodchain.catalog.dto.CreateOfferRequest;
import com.example.foodchain.catalog.dto.OfferResponse;
import com.example.foodchain.catalog.dto.OfferSnapshot;
import com.example.foodchain.catalog.dto.StockDecrement;
import com.example.foodchain.catalog.entity.Offer;
import com.example.foodchain.catalog.entity.PriceHistory;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.mapper.CatalogMapper;
import com.example.foodchain.catalog.repository.OfferRepository;
import com.example.foodchain.catalog.repository.PriceHistoryRepository;
import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.common.error.ForbiddenException;
import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductService productService;
    private final UserService userService;
    private final CatalogMapper mapper;

    public OfferService(OfferRepository offerRepository, PriceHistoryRepository priceHistoryRepository,
                        ProductService productService, UserService userService, CatalogMapper mapper) {
        this.offerRepository = offerRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.productService = productService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Transactional
    public OfferResponse create(UUID sellerId, CreateOfferRequest request) {
        User seller = userService.getById(sellerId);
        if (!seller.getRole().isSeller()) {
            throw new ForbiddenException("Seuls les agriculteurs peuvent publier des offres.");
        }
        Product product = productService.getById(request.productId());
        Offer offer = Offer.create(sellerId, product, request.quantity(), request.price(), request.location());
        offer = offerRepository.save(offer);
        priceHistoryRepository.save(PriceHistory.record(product.getId(), request.price()));
        return mapper.toOfferResponse(offer, seller.isVerified());
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> search(String category, String location, Boolean available) {
        return offerRepository.search(category, location, available).stream()
                .map(offer -> mapper.toOfferResponse(offer, userService.getById(offer.getSellerId()).isVerified()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> findBySeller(UUID sellerId) {
        boolean verified = userService.getById(sellerId).isVerified();
        return offerRepository.findBySellerId(sellerId).stream()
                .map(offer -> mapper.toOfferResponse(offer, verified))
                .toList();
    }

    @Transactional(readOnly = true)
    public OfferResponse getById(UUID id) {
        Offer offer = loadOffer(id);
        return mapper.toOfferResponse(offer, userService.getById(offer.getSellerId()).isVerified());
    }

    @Transactional(readOnly = true)
    public OfferSnapshot getSnapshot(UUID id) {
        return mapper.toSnapshot(loadOffer(id));
    }

    @Transactional(readOnly = true)
    public List<UUID> findOfferIdsBySeller(UUID sellerId) {
        return offerRepository.findBySellerId(sellerId).stream().map(Offer::getId).toList();
    }

    @Transactional
    public OfferResponse updateStock(UUID sellerId, UUID offerId, java.math.BigDecimal quantity) {
        Offer offer = loadOffer(offerId);
        if (!offer.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("Vous ne pouvez modifier que vos propres offres.");
        }
        offer.setQuantity(quantity);
        offer.setAvailable(quantity.signum() > 0);
        offer = offerRepository.save(offer);
        return mapper.toOfferResponse(offer, userService.getById(sellerId).isVerified());
    }

    /**
     * Locks every referenced offer (SELECT ... FOR UPDATE), verifies stock and
     * decrements atomically. MUST be called inside the caller's transaction so
     * that a failure rolls back the enclosing payment/order settlement.
     * Throws {@link ConflictException} (code INSUFFICIENT_STOCK) if any offer
     * cannot satisfy the demand — this is the guard against overselling.
     */
    @Transactional
    public void reserveAndDecrement(List<StockDecrement> decrements) {
        for (StockDecrement d : decrements) {
            Offer offer = offerRepository.lockById(d.offerId())
                    .orElseThrow(() -> new NotFoundException("Offre introuvable: " + d.offerId()));
            if (!offer.hasStock(d.quantity())) {
                throw new ConflictException("INSUFFICIENT_STOCK",
                        "Stock insuffisant pour l'offre " + offer.getId() + ".",
                        java.util.Map.of(
                                "offerId", offer.getId().toString(),
                                "requested", d.quantity(),
                                "available", offer.getQuantity()));
            }
            offer.decreaseStock(d.quantity());
            offerRepository.save(offer);
        }
    }

    private Offer loadOffer(UUID id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offre introuvable."));
    }
}
