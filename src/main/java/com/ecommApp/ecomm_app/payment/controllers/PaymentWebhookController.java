package com.ecommApp.ecomm_app.payment.controllers;

import com.ecommApp.ecomm_app.common.exceptions.PaymentRelatedException;
import com.ecommApp.ecomm_app.common.exceptions.ResourceNotFoundException;
import com.ecommApp.ecomm_app.payment.client.PaymentSimulatorClient;
import com.ecommApp.ecomm_app.payment.dto.PaymentWebhookRequest;
import com.ecommApp.ecomm_app.payment.services.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentWebhookService paymentWebhookService;
    private final PaymentSimulatorClient paymentSimulatorClient;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(
            @RequestBody PaymentWebhookRequest request
    ) {

//        System.out.println(
//                "Webhook received: " + request.eventId()
//        );
        try {
            String eventId =  paymentWebhookService.processWebhook(request);
            paymentSimulatorClient.acknowledgeWebhook(eventId);
        } catch (RuntimeException e) {
            throw  new PaymentRelatedException(
                   "Error occured in PaymentWebhookRequest"
            );
        }


        return ResponseEntity.ok().build();
    }
}