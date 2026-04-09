package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.repository.OrderRepository;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        List<OrderRequest> allPendingOrderForTrader = orderRepository.getAllPendingOrderForTrader(userName);
        if(allPendingOrderForTrader.size() > 3){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot have more than 3 pending orders");
        }

        int promisedStock = allPendingOrderForTrader.stream().filter(order -> order.getStock().equals(orderRequest.getStock()) && order.getSide().equals(OrderRequest.Side.SELL)).mapToInt(OrderRequest::getQuantity).sum();
        if(!(portfolioRepository.getUserHolding(userName, orderRequest.getStock()) - promisedStock > orderRequest.getQuantity())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have enough holdings to place this sell order considering the pending sell orders");
        }
        return orderRepository.placeOrder(orderRequest);
    }


    public String cancelOrder(long id) {
        OrderRequest order = orderRepository.getOrderById(id);
        if(!order.getState().equals(OrderRequest.State.PENDING)){
            return "ORDER CANNOT BE CANCELLED AS IT IS NOT IN PENDING STATE";
        }
        if(!order.getTraderId().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to cancel order");
        }

        orderRepository.updateOrderState(id, OrderRequest.State.CANCELLED);
        return "ORDER CANCELLED SUCCESSFULLY";
    }
}
