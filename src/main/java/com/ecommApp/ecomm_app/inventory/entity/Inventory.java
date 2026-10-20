package com.ecommApp.ecomm_app.inventory.entity;

import com.ecommApp.ecomm_app.products.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_inventory_product")
    )
    private Product product;

    @Column(nullable = false)
    private Integer availableQty = 0;

    @Column(nullable = false)
    private Integer reservedQty = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}