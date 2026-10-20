package com.ecommApp.ecomm_app.common.idempotency.repository;

import com.ecommApp.ecomm_app.common.idempotency.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository
        extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByUserIdAndIdempotencyKey(
            Long userId,
            String idempotencyKey
    );
}