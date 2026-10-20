package com.ecommApp.ecomm_app.common.audit.repository;

import com.ecommApp.ecomm_app.common.audit.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionLogRepository
        extends JpaRepository<TransactionLog, Long> {

    List<TransactionLog> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId
    );
}