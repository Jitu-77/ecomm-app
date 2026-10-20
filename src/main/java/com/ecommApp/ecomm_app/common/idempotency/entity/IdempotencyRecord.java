package com.ecommApp.ecomm_app.common.idempotency.entity;

import com.ecommApp.ecomm_app.common.enums.IdempotencyStatus;
import com.ecommApp.ecomm_app.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "idempotency_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_idempotency_key",
                        columnNames = {"user_id", "idempotency_key"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_idempotency_user")
    )
    private User user;

    @Column(nullable = false, length = 100)
    private String idempotencyKey;

    @Column(nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdempotencyStatus status;

    private Integer responseStatus;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}