package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.repository.OrderRepository;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final PortfolioRepository portfolioRepository;

    public long placeOrder(OrderRequest orderRequest) {
        Instant now = Instant.now();
        orderRequest.setExpiresAt(now.plus(24, ChronoUnit.HOURS));
        orderRequest.setCreatedAt(now);
        orderRequest.setState(OrderRequest.State.PENDING);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!orderRequest.getTraderId().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to place this order");
        }

        if (orderRequest.getSide().equals(OrderRequest.Side.SELL) && orderRequest.getQuantity() > portfolioRepository.getUserHolding(userName, orderRequest.getStock())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have enough holdings to place this sell order");
        }

        return orderRepository.placeOrder(orderRequest);
    }


}
