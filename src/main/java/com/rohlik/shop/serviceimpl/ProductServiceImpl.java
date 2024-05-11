package com.rohlik.shop.serviceimpl;

import com.rohlik.shop.entity.ProductEntity;
import com.rohlik.shop.exception.ApiException;
import com.rohlik.shop.model.ProductRequestDTO;
import com.rohlik.shop.model.ProductResponseDTO;
import com.rohlik.shop.repository.ProductRepository;
import com.rohlik.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void create(ProductRequestDTO productRequestDTO) {
        val productEntity = ProductEntity.builder()
                .name(productRequestDTO.getName())
                .price(productRequestDTO.getPrice())
                .quantity(productRequestDTO.getQuantity())
                .build();

        productRepository.save(productEntity);

        log.info("Product '{}' successfully created", productEntity.getId());
    }

    @Override
    public void update(Long productId, ProductRequestDTO productRequestDTO) {
        val productEntity = productRepository.findById(productId).orElseThrow(() ->
                ApiException.productNotFound(productId));

        productEntity.setName(productRequestDTO.getName());
        productEntity.setPrice(productRequestDTO.getPrice());
        productEntity.setQuantity(productRequestDTO.getQuantity());

        productRepository.save(productEntity);

        log.info("Product '{}' successfully updated", productEntity.getId());
    }

    @Override
    public void delete(Long productId) {
        val productEntity = productRepository.findById(productId).orElseThrow(() ->
                ApiException.productNotFound(productId));

        productRepository.delete(productEntity);

        log.info("Product '{}' successfully deleted", productEntity.getId());
    }

    @Override
    public List<ProductResponseDTO> getAll() {
        val productResponseDTOList = new ArrayList<ProductResponseDTO>();
        productRepository.findAll().forEach(productEntity -> {
            val productResponsesDTO = ProductResponseDTO.builder()
                    .id(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .quantity(productEntity.getQuantity())
                    .build();

            productResponseDTOList.add(productResponsesDTO);
        });
        return productResponseDTOList;
    }
}
