package com.example.foodchain.catalog.controller;

import com.example.foodchain.catalog.dto.CreateProductRequest;
import com.example.foodchain.catalog.dto.ProductResponse;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.mapper.CatalogMapper;
import com.example.foodchain.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Catalogue - Produits")
public class ProductController {

    private final ProductService productService;
    private final CatalogMapper mapper;

    public ProductController(ProductService productService, CatalogMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    @Operation(summary = "Lister les produits de référence (public)")
    @GetMapping
    public List<ProductResponse> list() {
        return productService.list().stream().map(mapper::toProductResponse).toList();
    }

    @Operation(summary = "Créer un produit de référence (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProductResponse(product));
    }
}
