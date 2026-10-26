package com.ecommApp.ecomm_app.payment.client;

import com.ecommApp.ecomm_app.payment.dto.PaymentSimulatorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentSimulatorClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${payment.simulator.base-url}")
    private String baseUrl;

    public Optional<PaymentSimulatorResponse> getPayment(
            String paymentReference
    ) {

        try {

            RestClient restClient = restClientBuilder
                    .baseUrl(baseUrl)
                    .build();

            PaymentSimulatorResponse response = restClient
                    .get()
                    .uri("/api/payments/{paymentReference}", paymentReference)
                    .retrieve()
                    .body(PaymentSimulatorResponse.class);

            return Optional.ofNullable(response);

        } catch (HttpClientErrorException.NotFound ex) {

            return Optional.empty();
        }
    }
}