package com.rohlik.shop.service;

import com.rohlik.shop.model.OrderProductRequestDTO;
import com.rohlik.shop.model.OrderRequestDTO;
import com.rohlik.shop.model.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    List<OrderProductRequestDTO> create(OrderRequestDTO orderRequestDTO);

    void cancel(Long orderId);

    void cancelAllExpired();

    void pay(Long orderId);

    List<OrderResponseDTO> getAll();
}
