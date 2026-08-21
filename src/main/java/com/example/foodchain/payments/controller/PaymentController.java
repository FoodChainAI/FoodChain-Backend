package com.example.foodchain.payments.controller;

import com.example.foodchain.common.security.SecurityUtils;
import com.example.foodchain.payments.dto.CreatePaymentRequest;
import com.example.foodchain.payments.dto.PaymentCallbackRequest;
import com.example.foodchain.payments.dto.PaymentResponse;
import com.example.foodchain.payments.entity.Payment;
import com.example.foodchain.payments.mapper.PaymentMapper;
import com.example.foodchain.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Paiements")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @Operation(summary = "Initier un paiement pour une commande CONFIRMEE")
    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.initiate(
                SecurityUtils.currentUserId(),
                request.orderId(),
                request.method());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponse(payment));
    }

    @Operation(summary = "Callback passerelle de paiement (public — appelé par le fournisseur Mobile Money)")
    @PostMapping("/callback")
    public PaymentResponse callback(@Valid @RequestBody PaymentCallbackRequest request) {
        Payment payment = paymentService.handleCallback(request.reference(), request.status());
        return paymentMapper.toResponse(payment);
    }
}
