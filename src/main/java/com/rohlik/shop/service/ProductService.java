package com.rohlik.shop.service;

import com.rohlik.shop.model.ProductRequestDTO;
import com.rohlik.shop.model.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    void create(ProductRequestDTO productRequestDTO);

    List<ProductResponseDTO> getAll();

    void update(Long productId, ProductRequestDTO productRequestDTO);

    void delete(Long productId);
}
