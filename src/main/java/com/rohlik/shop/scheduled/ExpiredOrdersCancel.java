package com.rohlik.shop.scheduled;

import com.rohlik.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredOrdersCancel {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 60000)
    public void cancelAllExpiredOrders() {
        orderService.cancelAllExpired();
    }
}
