package com.rohlik.shop.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductResponseDTO {

    private Long productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;
}
