package com.rohlik.shop.controller;

import com.rohlik.shop.model.ProductRequestDTO;
import com.rohlik.shop.model.ProductResponseDTO;
import com.rohlik.shop.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody @NotNull @Valid ProductRequestDTO productRequestDTO) {
        productService.create(productRequestDTO);
    }

    @GetMapping
    public List<ProductResponseDTO> getAll() {
        return productService.getAll();
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable @NotNull @Positive Long productId,
            @RequestBody @NotNull @Valid ProductRequestDTO productRequestDTO
    ) {
        productService.update(productId, productRequestDTO);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull @Positive Long productId) {
        productService.delete(productId);
    }
}
