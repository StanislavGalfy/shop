package com.rohlik.shop.model;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;

    private OrderState state;

    private Instant created;

    private List<OrderProductResponseDTO> products;
}
