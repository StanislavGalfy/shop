package com.rohlik.shop.serviceimpl;

import com.rohlik.shop.entity.OrderEntity;
import com.rohlik.shop.entity.OrderProductEntity;
import com.rohlik.shop.entity.ProductEntity;
import com.rohlik.shop.exception.ApiException;
import com.rohlik.shop.model.OrderProductRequestDTO;
import com.rohlik.shop.model.OrderRequestDTO;
import com.rohlik.shop.model.OrderState;
import com.rohlik.shop.repository.OrderRepository;
import com.rohlik.shop.repository.ProductRepository;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    private static final Long ORDER_ID = 1L;

    private static final Long ORDER_CREATED_EPOCH_MILLIS = 1715447158790L;

    private static final Long FIRST_PRODUCT_ID = 1L;

    private static final String FIRST_PRODUCT_NAME = "first product";

    private static final Integer FIRST_PRODUCT_STOCK_QUANTITY = 10;

    private static final Integer FIRST_ORDER_PRODUCT_QUANTITY = 30;

    private static final Integer FIRST_PRODUCT_ORDER_QUANTITY = 5;

    private static final BigDecimal FIRST_PRODUCT_PRICE = BigDecimal.valueOf(100);

    private static final Long SECOND_PRODUCT_ID = 2L;

    private static final String SECOND_PRODUCT_NAME = "second product";

    private static final Integer SECOND_PRODUCT_STOCK_QUANTITY = 20;

    private static final Integer SECOND_PRODUCT_ORDER_QUANTITY = 15;

    private static final Integer SECOND_ORDER_PRODUCT_QUANTITY = 40;

    private static final Integer SECOND_PRODUCT_ORDER_EXCEEDED_QUANTITY = 25;

    private static final BigDecimal SECOND_PRODUCT_PRICE = BigDecimal.valueOf(200);

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    @Captor
    ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;

    @Captor
    ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor;


    @Test
    public void createOrderTest() {
        setUpProductDatabaseMocks(true);
        val orderRequestDTO = getOrderRequestDTO(SECOND_PRODUCT_ORDER_QUANTITY);

        orderService.create(orderRequestDTO);

        Mockito.verify(productRepository, Mockito.times(2)).save(
                productEntityArgumentCaptor.capture());

        val productEntities = productEntityArgumentCaptor.getAllValues();
        Assertions.assertEquals(2, productEntities.size());
        Assertions.assertEquals(FIRST_PRODUCT_ID, productEntities.getFirst().getId());
        Assertions.assertEquals(FIRST_PRODUCT_NAME, productEntities.getFirst().getName());
        Assertions.assertEquals(FIRST_PRODUCT_STOCK_QUANTITY - FIRST_PRODUCT_ORDER_QUANTITY,
                productEntities.getFirst().getQuantity());
        Assertions.assertEquals(SECOND_PRODUCT_ID, productEntities.get(1).getId());
        Assertions.assertEquals(SECOND_PRODUCT_NAME, productEntities.get(1).getName());
        Assertions.assertEquals(SECOND_PRODUCT_STOCK_QUANTITY - SECOND_PRODUCT_ORDER_QUANTITY,
                productEntities.get(1).getQuantity());

        Mockito.verify(orderRepository).save(orderEntityArgumentCaptor.capture());
        val orderEntity = orderEntityArgumentCaptor.getValue();
        Assertions.assertNotNull(orderEntity.getCreated());
        Assertions.assertEquals(OrderState.CREATED, orderEntity.getState());
        Assertions.assertEquals(2, orderEntity.getOrderProducts().size());

        Assertions.assertEquals(FIRST_PRODUCT_ID, orderEntity.getOrderProducts().getFirst().getProductId());
        Assertions.assertEquals(FIRST_PRODUCT_NAME, orderEntity.getOrderProducts().getFirst().getName());
        Assertions.assertEquals(FIRST_PRODUCT_PRICE, orderEntity.getOrderProducts().getFirst().getPrice());
        Assertions.assertEquals(FIRST_PRODUCT_ORDER_QUANTITY, orderEntity.getOrderProducts().getFirst().getQuantity());

        Assertions.assertEquals(SECOND_PRODUCT_ID, orderEntity.getOrderProducts().get(1).getProductId());
        Assertions.assertEquals(SECOND_PRODUCT_NAME, orderEntity.getOrderProducts().get(1).getName());
        Assertions.assertEquals(SECOND_PRODUCT_PRICE, orderEntity.getOrderProducts().get(1).getPrice());
        Assertions.assertEquals(SECOND_PRODUCT_ORDER_QUANTITY, orderEntity.getOrderProducts().get(1).getQuantity());

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void createOrderInsufficientQuantityTest() {
        setUpProductDatabaseMocks(true);
        val orderRequestDTO = getOrderRequestDTO(SECOND_PRODUCT_ORDER_EXCEEDED_QUANTITY);

        val orderProductResponseDTOs = orderService.create(orderRequestDTO);

        Assertions.assertEquals(1, orderProductResponseDTOs.size());
        Assertions.assertEquals(SECOND_PRODUCT_ID, orderProductResponseDTOs.getFirst().getProductId());
        Assertions.assertEquals(SECOND_PRODUCT_ORDER_EXCEEDED_QUANTITY - SECOND_PRODUCT_STOCK_QUANTITY,
                orderProductResponseDTOs.getFirst().getQuantity());

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void createOrderInvalidProductIdTest() {
        setUpProductDatabaseMocks(false);
        val orderRequestDTO = getOrderRequestDTO(SECOND_PRODUCT_ORDER_QUANTITY);

        Assertions.assertThrows(ApiException.class, () -> orderService.create(orderRequestDTO));

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void cancelOrderTest() {
        setUpProductDatabaseMocks(true);
        Mockito.when(orderRepository.findById(Mockito.eq(ORDER_ID))).thenReturn(Optional.of(getOrderEntity()));

        orderService.cancel(ORDER_ID);

        verifyCanceledOrder();
    }

    @Test
    public void cancelOrderMissingProductTest() {
        setUpProductDatabaseMocks(false);
        Mockito.when(orderRepository.findById(Mockito.eq(ORDER_ID))).thenReturn(Optional.of(getOrderEntity()));

        orderService.cancel(ORDER_ID);

        Mockito.verify(productRepository, Mockito.times(1)).save(
                productEntityArgumentCaptor.capture());

        val productEntities = productEntityArgumentCaptor.getAllValues();
        Assertions.assertEquals(1, productEntities.size());
        Assertions.assertEquals(FIRST_PRODUCT_ID, productEntities.getFirst().getId());
        Assertions.assertEquals(FIRST_PRODUCT_NAME, productEntities.getFirst().getName());
        Assertions.assertEquals(FIRST_PRODUCT_STOCK_QUANTITY + FIRST_ORDER_PRODUCT_QUANTITY,
                productEntities.getFirst().getQuantity());

        Mockito.verify(orderRepository).save(orderEntityArgumentCaptor.capture());
        val orderEntity = orderEntityArgumentCaptor.getValue();
        Assertions.assertNotNull(orderEntity.getCreated());
        Assertions.assertEquals(OrderState.CANCELED, orderEntity.getState());
        Assertions.assertEquals(2, orderEntity.getOrderProducts().size());

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void cancelOrderInvalidOrderIdTest() {
        Mockito.when(orderRepository.findById(Mockito.eq(ORDER_ID))).thenReturn(Optional.empty());

        Assertions.assertThrows(ApiException.class, () -> orderService.cancel(ORDER_ID));
    }

    @Test
    public void payOrderTest() {
        Mockito.when(orderRepository.findById(Mockito.eq(ORDER_ID))).thenReturn(Optional.of(getOrderEntity()));

        orderService.pay(ORDER_ID);

        Mockito.verify(orderRepository).save(orderEntityArgumentCaptor.capture());
        val orderEntity = orderEntityArgumentCaptor.getValue();
        Assertions.assertNotNull(orderEntity.getCreated());
        Assertions.assertEquals(OrderState.PAYED, orderEntity.getState());
        Assertions.assertEquals(2, orderEntity.getOrderProducts().size());

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void cancelAllExpiredOrdersTest() {
        setUpProductDatabaseMocks(true);
        Mockito.when(orderRepository.findByStateAndCreatedLessThanEqual(Mockito.eq(OrderState.CREATED),
                Mockito.any(Instant.class))).thenReturn(List.of(getOrderEntity()));

        ReflectionTestUtils.setField(orderService, "orderExpirationInMillis", 60000L);
        orderService.cancelAllExpired();

        verifyCanceledOrder();
    }

    @Test
    public void payOrderInvalidOrderIdTest() {
        Mockito.when(orderRepository.findById(Mockito.eq(ORDER_ID))).thenReturn(Optional.empty());

        Assertions.assertThrows(ApiException.class, () -> orderService.pay(ORDER_ID));

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    private void setUpProductDatabaseMocks(boolean addSecondProduct) {
        val firstProductEntity = ProductEntity.builder()
                .id(FIRST_PRODUCT_ID)
                .name(FIRST_PRODUCT_NAME)
                .price(FIRST_PRODUCT_PRICE)
                .quantity(FIRST_PRODUCT_STOCK_QUANTITY)
                .build();

        val secondProductEntity = ProductEntity.builder()
                .id(SECOND_PRODUCT_ID)
                .name(SECOND_PRODUCT_NAME)
                .price(SECOND_PRODUCT_PRICE)
                .quantity(SECOND_PRODUCT_STOCK_QUANTITY)
                .build();

        Mockito.when(productRepository.findById(Mockito.eq(FIRST_PRODUCT_ID)))
                .thenReturn(Optional.of(firstProductEntity));
        Mockito.when(productRepository.findById(Mockito.eq(SECOND_PRODUCT_ID)))
                .thenReturn(addSecondProduct ? Optional.of(secondProductEntity) : Optional.empty());
    }

    private OrderEntity getOrderEntity() {
        val firstOrderProductEntity = OrderProductEntity.builder()
                .id(FIRST_PRODUCT_ID)
                .productId(FIRST_PRODUCT_ID)
                .name(FIRST_PRODUCT_NAME)
                .price(FIRST_PRODUCT_PRICE)
                .quantity(FIRST_ORDER_PRODUCT_QUANTITY)
                .build();

        val secondOrderProductEntity = OrderProductEntity.builder()
                .id(SECOND_PRODUCT_ID)
                .productId(SECOND_PRODUCT_ID)
                .name(SECOND_PRODUCT_NAME)
                .price(SECOND_PRODUCT_PRICE)
                .quantity(SECOND_ORDER_PRODUCT_QUANTITY)
                .build();

        return OrderEntity.builder()
                .created(Instant.ofEpochSecond(ORDER_CREATED_EPOCH_MILLIS))
                .state(OrderState.CREATED)
                .orderProducts(List.of(firstOrderProductEntity, secondOrderProductEntity))
                .build();
    }

    private OrderRequestDTO getOrderRequestDTO(Integer secondProductQuantity) {
        val firstOrderProductDT0 = OrderProductRequestDTO.builder()
                .productId(FIRST_PRODUCT_ID)
                .quantity(FIRST_PRODUCT_ORDER_QUANTITY)
                .build();

        val secondOrderProductDT0 = OrderProductRequestDTO.builder()
                .productId(SECOND_PRODUCT_ID)
                .quantity(secondProductQuantity)
                .build();

        return OrderRequestDTO.builder()
                .products(List.of(firstOrderProductDT0, secondOrderProductDT0))
                .build();
    }

    private void verifyCanceledOrder() {
        Mockito.verify(productRepository, Mockito.times(2)).save(
                productEntityArgumentCaptor.capture());

        val productEntities = productEntityArgumentCaptor.getAllValues();
        Assertions.assertEquals(2, productEntities.size());
        Assertions.assertEquals(FIRST_PRODUCT_ID, productEntities.getFirst().getId());
        Assertions.assertEquals(FIRST_PRODUCT_NAME, productEntities.getFirst().getName());
        Assertions.assertEquals(FIRST_PRODUCT_STOCK_QUANTITY + FIRST_ORDER_PRODUCT_QUANTITY,
                productEntities.getFirst().getQuantity());
        Assertions.assertEquals(SECOND_PRODUCT_ID, productEntities.get(1).getId());
        Assertions.assertEquals(SECOND_PRODUCT_NAME, productEntities.get(1).getName());
        Assertions.assertEquals(SECOND_PRODUCT_STOCK_QUANTITY + SECOND_ORDER_PRODUCT_QUANTITY,
                productEntities.get(1).getQuantity());

        Mockito.verify(orderRepository).save(orderEntityArgumentCaptor.capture());
        val orderEntity = orderEntityArgumentCaptor.getValue();
        Assertions.assertNotNull(orderEntity.getCreated());
        Assertions.assertEquals(OrderState.CANCELED, orderEntity.getState());
        Assertions.assertEquals(2, orderEntity.getOrderProducts().size());

        Mockito.verifyNoMoreInteractions(productRepository);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }
}
