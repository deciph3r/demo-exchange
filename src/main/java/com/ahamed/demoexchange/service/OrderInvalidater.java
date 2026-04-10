package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
@AllArgsConstructor
@Slf4j
public class OrderInvalidater implements SchedulingConfigurer {
    final OrderRepository orderRepository;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedDelayTask(this::invalidateExpiredOrders, Duration.ofSeconds(30));
    }

    private void invalidateExpiredOrders() {
        log.info("invalidateExpiredOrders");
        orderRepository.getAllPendingOrders().stream()
                .filter(order -> order.getExpiresAt().isBefore(Instant.now()))
                .forEach(this::invalidateOrder);
    }

    @Transactional
    private void invalidateOrder(OrderRequest order) {
        log.info("invalidating order {}", order);
        orderRepository.getOrderById(order.getId());
        orderRepository.updateOrderState(order.getId(), OrderRequest.State.CANCELLED, "ORDER EXPIRED");
    }
}
