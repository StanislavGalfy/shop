package com.rohlik.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    private OrderEntity order;

    @Column(name="product_id")
    private Long productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;
}
