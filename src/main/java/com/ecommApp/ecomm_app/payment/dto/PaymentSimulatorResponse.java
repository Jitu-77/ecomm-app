package com.ecommApp.ecomm_app.payment.dto;

import java.math.BigDecimal;

public record PaymentSimulatorResponse(
        String paymentReference,
        BigDecimal amount,
        String status,
        String transactionRef
) {
}
