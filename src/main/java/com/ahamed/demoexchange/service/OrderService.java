package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;

    public long placeOrder(OrderRequest orderRequest){
        Instant now = Instant.now();
        orderRequest.setExpiresAt(now.plus(24, ChronoUnit.HOURS));
        orderRequest.setCreatedAt(now);
        orderRequest.setState(OrderRequest.State.PENDING);

        return orderRepository.placeOrder(orderRequest);
    }
}
