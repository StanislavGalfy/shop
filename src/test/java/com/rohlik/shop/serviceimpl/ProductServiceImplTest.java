package com.rohlik.shop.serviceimpl;

import com.rohlik.shop.entity.ProductEntity;
import com.rohlik.shop.exception.ApiException;
import com.rohlik.shop.model.ProductRequestDTO;
import com.rohlik.shop.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    private static final Long PRODUCT_ID = 1L;

    private static final String PRODUCT_NAME = "product";

    private static final Integer PRODUCT_QUANTITY = 25;

    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10);

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Captor
    ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;

    @Test
    public void createProductTest() {
        productService.create(getProductRequestDTO());

        Mockito.verify(productRepository).save(productEntityArgumentCaptor.capture());
        assertProductEntity(productEntityArgumentCaptor.getValue());
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void deleteProductTest() {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getProductEntity()));

        productService.delete(1L);

        Mockito.verify(productRepository).delete(productEntityArgumentCaptor.capture());
        assertProductEntity(productEntityArgumentCaptor.getValue());
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void deleteNonExistingProductTest() {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ApiException.class, () -> productService.delete( PRODUCT_ID));
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void updateProductTest() {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getProductEntity()));

        productService.update(1L, getProductRequestDTO());

        Mockito.verify(productRepository).save(productEntityArgumentCaptor.capture());
        assertProductEntity(productEntityArgumentCaptor.getValue());
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void updateNonExistingProductTest() {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ApiException.class, () -> productService.update( PRODUCT_ID, getProductRequestDTO()));
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    private void assertProductEntity(ProductEntity productEntity) {
        Assertions.assertEquals(PRODUCT_NAME, productEntity.getName());
        Assertions.assertEquals(PRODUCT_PRICE, productEntity.getPrice());
        Assertions.assertEquals(PRODUCT_QUANTITY, productEntity.getQuantity());
    }

    private ProductRequestDTO getProductRequestDTO() {
        return ProductRequestDTO.builder()
                .name(PRODUCT_NAME)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .build();
    }

    private ProductEntity getProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .name(PRODUCT_NAME)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .build();
    }
}
