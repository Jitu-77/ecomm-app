package com.ecommApp.ecomm_app.inventory.entity;

import com.ecommApp.ecomm_app.common.enums.ReservationStatus;
import com.ecommApp.ecomm_app.order.entity.Order;
import com.ecommApp.ecomm_app.payment.entity.Payment;
import com.ecommApp.ecomm_app.products.entity.Product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_reservations"
//        ,        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_reservation_order_product",
//                        columnNames = {"order_id", "product_id"}
//                )
//        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(
//            name = "order_id",
//            nullable = false,
//            foreignKey = @ForeignKey(name = "fk_reservation_order")
//    )
//    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "payment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reservation_payment")
    )
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reservation_product")
    )
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}