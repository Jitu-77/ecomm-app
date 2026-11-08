package com.ecommApp.ecomm_app.payment.services;

import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.order.services.OrderService;
import com.ecommApp.ecomm_app.payment.dto.PaymentWebhookRequest;
import com.ecommApp.ecomm_app.payment.entity.WebhookEvent;
import com.ecommApp.ecomm_app.payment.repository.WebhookEventRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final WebhookEventRepository webhookEventRepository;
    private final OrderService orderService;

    @Transactional
    public String processWebhook(PaymentWebhookRequest request) {

        Optional<WebhookEvent> existingEvent =
                webhookEventRepository.findByEventId(request.eventId());

        if (existingEvent.isPresent()) {
            System.out.println(
                    "Duplicate webhook ignored: "
                            + request.eventId()
            );
            return request.eventId();
        }

        WebhookEvent webhookEvent = WebhookEvent.builder()
                .eventId(request.eventId())
                .paymentReference(request.paymentReference())
                .paymentStatus(request.status())
                .build();

        webhookEventRepository.save(webhookEvent);

        System.out.println(
                "Webhook registered: "
                        + request.eventId()
        );
        if (request.status() == PaymentStatus.SUCCESS) {

            orderService.processSuccessfulPayment(
                    request.paymentReference()
            );

        } else if (request.status() == PaymentStatus.FAILED) {

            orderService.processFailedPayment(
                    request.paymentReference()
            );
        }

        return request.eventId();
    }
}
