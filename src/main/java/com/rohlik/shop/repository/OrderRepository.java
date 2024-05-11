package com.rohlik.shop.repository;

import com.rohlik.shop.entity.OrderEntity;
import com.rohlik.shop.model.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStateAndCreatedLessThanEqual(OrderState state, Instant created);
}
