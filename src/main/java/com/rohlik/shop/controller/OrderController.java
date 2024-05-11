package com.rohlik.shop.controller;

import com.rohlik.shop.model.OrderProductRequestDTO;
import com.rohlik.shop.model.OrderRequestDTO;
import com.rohlik.shop.model.OrderResponseDTO;
import com.rohlik.shop.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public List<OrderProductRequestDTO> create(@RequestBody @NotNull @Valid OrderRequestDTO orderRequestDTO) {
        return orderService.create(orderRequestDTO);
    }

    @GetMapping
    public List<OrderResponseDTO> getAll() {
        return orderService.getAll();
    }

    @PostMapping("{orderId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable @NotNull @Positive Long orderId) {
        orderService.cancel(orderId);
    }

    @PostMapping("{orderId}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pay(@PathVariable @NotNull @Positive Long orderId) {
        orderService.pay(orderId);
    }
}
