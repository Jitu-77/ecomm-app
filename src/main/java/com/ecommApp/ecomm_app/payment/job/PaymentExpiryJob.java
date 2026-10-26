package com.ecommApp.ecomm_app.payment.job;

import com.ecommApp.ecomm_app.common.enums.PaymentStatus;
import com.ecommApp.ecomm_app.payment.client.PaymentSimulatorClient;
import com.ecommApp.ecomm_app.payment.dto.PaymentSimulatorResponse;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import com.ecommApp.ecomm_app.payment.repository.PaymentRepository;

import com.ecommApp.ecomm_app.payment.services.PaymentExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpiryJob {

    private final PaymentRepository paymentRepository;
    private final PaymentSimulatorClient paymentSimulatorClient;
    private final PaymentExpiryService paymentExpiryService;

    @Scheduled(
            fixedDelayString = "${payment.expiry.scheduler.interval-ms:60000}"
    )
    public void processExpiredPayments() {

        LocalDateTime now = LocalDateTime.now();

        List<Payment> expiredPayments =
                paymentRepository.findByStatusAndExpiresAtBefore(
                        PaymentStatus.PENDING,
                        now
                );

        for (Payment payment : expiredPayments) {

            try {

                Optional<PaymentSimulatorResponse> simulatorPayment =
                        paymentSimulatorClient.getPayment(
                                payment.getPaymentReference()
                        );

                if (simulatorPayment.isEmpty()) {

                    log.info(
                            "Payment {} not found in Payment Simulator. Expiring payment.",
                            payment.getPaymentReference()
                    );

                    paymentExpiryService.expirePayment(payment.getId());

                    continue;
                }

                PaymentSimulatorResponse response =
                        simulatorPayment.get();

                log.info(
                        "Payment {} found in simulator with status {}",
                        payment.getPaymentReference(),
                        response.status()
                );

                /*
                 * SUCCESS will be handled when Order module
                 * and successful-payment processing are implemented.
                 *
                 * FAILED will also be handled as a release flow.
                 */

            } catch (Exception ex) {

                log.error(
                        "Error while reconciling payment {}",
                        payment.getPaymentReference(),
                        ex
                );
            }
        }
    }
}