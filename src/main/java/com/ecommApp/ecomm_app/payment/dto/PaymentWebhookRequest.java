package com.ecommApp.ecomm_app.payment.dto;

import com.ecommApp.ecomm_app.common.enums.PaymentStatus;

public record PaymentWebhookRequest(
        String eventId,
        String paymentReference,
        String transactionRef,
        PaymentStatus status
) {
}
