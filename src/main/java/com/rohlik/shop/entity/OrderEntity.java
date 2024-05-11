package com.rohlik.shop.entity;

import com.rohlik.shop.model.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "order_")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;

    private OrderState state;

    private Instant created;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL})
    private List<OrderProductEntity> orderProducts;
}
