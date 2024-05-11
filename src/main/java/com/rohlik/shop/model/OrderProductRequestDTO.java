package com.rohlik.shop.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    @Max(1000000)
    private Integer quantity;
}
