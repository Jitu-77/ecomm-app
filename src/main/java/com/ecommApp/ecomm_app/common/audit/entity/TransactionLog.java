package com.ecommApp.ecomm_app.common.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(length = 30)
    private String oldStatus;

    @Column(length = 30)
    private String newStatus;

    private Long createdBy;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}