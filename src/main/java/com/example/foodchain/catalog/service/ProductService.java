package com.example.foodchain.catalog.service;

import com.example.foodchain.catalog.dto.CreateProductRequest;
import com.example.foodchain.catalog.entity.PriceHistory;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.repository.PriceHistoryRepository;
import com.example.foodchain.catalog.repository.ProductRepository;
import com.example.foodchain.common.error.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public ProductService(ProductRepository productRepository, PriceHistoryRepository priceHistoryRepository) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> list() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable."));
    }

    @Transactional
    public Product create(CreateProductRequest request) {
        Product product = Product.create(request.name(), request.category(), request.unit(), request.basePrice());
        product = productRepository.save(product);
        priceHistoryRepository.save(PriceHistory.record(product.getId(), product.getBasePrice()));
        return product;
    }
}
