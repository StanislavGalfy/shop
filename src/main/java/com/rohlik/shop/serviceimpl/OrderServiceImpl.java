package com.rohlik.shop.serviceimpl;

import com.rohlik.shop.entity.OrderEntity;
import com.rohlik.shop.entity.OrderProductEntity;
import com.rohlik.shop.model.*;
import com.rohlik.shop.entity.ProductEntity;
import com.rohlik.shop.exception.ApiException;
import com.rohlik.shop.repository.OrderRepository;
import com.rohlik.shop.repository.ProductRepository;
import com.rohlik.shop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final Long orderExpirationInMillis;

    public OrderServiceImpl(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            @Value("${order.expiration.millis}") Long orderExpirationInMillis
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderExpirationInMillis = orderExpirationInMillis;
    }

    @Override
    @Transactional
    public List<OrderProductRequestDTO> create(OrderRequestDTO orderRequestDTO) {
        val missingProductDTOs = new ArrayList<OrderProductRequestDTO>();
        val productEntitiesToUpdate = new ArrayList<ProductEntity>();

        val orderProductEntities = new ArrayList<OrderProductEntity>();
        val orderEntity = OrderEntity.builder()
                .state(OrderState.CREATED)
                .created(Instant.now())
                .orderProducts(orderProductEntities)
                .build();

        orderRequestDTO.getProducts().forEach(orderProductRequestDTO -> {
            val productId = orderProductRequestDTO.getProductId();
            val productEntity = productRepository.findById(productId).orElseThrow(() ->
                    ApiException.productNotFound(productId));

            if (productEntity.getQuantity() < orderProductRequestDTO.getQuantity()) {
                val missingProduct = OrderProductRequestDTO.builder()
                        .productId(productEntity.getId())
                        .quantity(orderProductRequestDTO.getQuantity() - productEntity.getQuantity())
                        .build();
                missingProductDTOs.add(missingProduct);
                return;
            }

            productEntity.setQuantity(productEntity.getQuantity() - orderProductRequestDTO.getQuantity());
            productEntitiesToUpdate.add(productEntity);

            val orderProductEntity = OrderProductEntity.builder()
                    .order(orderEntity)
                    .productId(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .quantity(orderProductRequestDTO.getQuantity())
                    .build();

            orderProductEntities.add(orderProductEntity);
        });

        if (!missingProductDTOs.isEmpty()) {

            log.info("Order not created because of missing products");

            return missingProductDTOs;
        }

        productEntitiesToUpdate.forEach(productRepository::save);
        orderRepository.save(orderEntity);

        log.info("New order '{}' successfully created", orderEntity.getId());

        return Collections.emptyList();
    }

    @Override
    public List<OrderResponseDTO> getAll() {
        val orderEntities = orderRepository.findAll();
        val orderResponseDTOs = new ArrayList<OrderResponseDTO>();

        orderEntities.forEach(orderEntity -> {
            val orderProductResponseDTOs = new ArrayList<OrderProductResponseDTO>();
            orderEntity.getOrderProducts().forEach(orderProductEntity -> {
                val orderProductResponseDTO = OrderProductResponseDTO.builder()
                        .productId(orderProductEntity.getProductId())
                        .name(orderProductEntity.getName())
                        .price(orderProductEntity.getPrice())
                        .quantity(orderProductEntity.getQuantity())
                        .build();
                orderProductResponseDTOs.add(orderProductResponseDTO);
            });

            val orderResponseDTO = OrderResponseDTO.builder()
                    .id(orderEntity.getId())
                    .state(orderEntity.getState())
                    .created(orderEntity.getCreated())
                    .products(orderProductResponseDTOs)
                    .build();
            orderResponseDTOs.add(orderResponseDTO);
        });
        return orderResponseDTOs;
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        val orderEntity = orderRepository.findById(orderId).orElseThrow(() -> ApiException.orderNotFound(orderId));
        assertOrderState(orderEntity);
        this.cancel(orderEntity);

        log.info("Order '{}' successfully canceled", orderEntity.getId());
    }

    @Override
    public void pay(Long orderId) {
        val orderEntity = orderRepository.findById(orderId).orElseThrow(() -> ApiException.orderNotFound(orderId));
        assertOrderState(orderEntity);

        orderEntity.setState(OrderState.PAYED);
        orderRepository.save(orderEntity);

        log.info("Order '{}' successfully payed", orderEntity.getId());
    }

    @Override
    @Transactional
    public void cancelAllExpired() {
        val expiredOrderEntities = orderRepository.findByStateAndCreatedLessThanEqual(OrderState.CREATED,
                Instant.now().minusMillis(orderExpirationInMillis));
        expiredOrderEntities.forEach(this::cancel);

        log.info("{} expired orders successfully canceled", expiredOrderEntities.size());
    }

    private void assertOrderState(OrderEntity orderEntity) {
        if (OrderState.PAYED.equals(orderEntity.getState())) {
            throw ApiException.orderAlreadyPayed(orderEntity.getId());
        }
        if (OrderState.CANCELED.equals(orderEntity.getState())) {
            throw ApiException.orderAlreadyCanceled(orderEntity.getId());
        }
    }

    private void cancel(OrderEntity orderEntity) {
        orderEntity.getOrderProducts().forEach(orderProductEntity -> {
            val productEntityOpt = productRepository.findById(orderProductEntity.getProductId());
            if (productEntityOpt.isEmpty()) {
                return;
            }
            val productEntity = productEntityOpt.get();
            productEntity.setQuantity(productEntity.getQuantity() + orderProductEntity.getQuantity());
            productRepository.save(productEntity);
        });

        orderEntity.setState(OrderState.CANCELED);
        orderRepository.save(orderEntity);
    }
}
