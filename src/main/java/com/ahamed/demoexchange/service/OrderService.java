package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.repository.OrderRepository;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    final OrderRepository orderRepository;
    final PortfolioRepository portfolioRepository;

    public long placeOrder(OrderRequest orderRequest) {
        Instant now = Instant.now();
        orderRequest.setExpiresAt(now.plus(24, ChronoUnit.HOURS));
        orderRequest.setCreatedAt(now);
        orderRequest.setState(OrderRequest.State.PENDING);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        orderRequest.setTraderId(userName);
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
        OrderRequest currentOrder = orderRepository.placeOrder(orderRequest);
        log.info("Order placed successfully :{}", currentOrder);
        OrderRequest orderToTrade = getOrderToTrade(orderRequest);
        log.info("Found trade successfully :{}", orderToTrade);
        trade(currentOrder, orderToTrade);
        return currentOrder.getId();
    }
    private boolean isPriceMatch(OrderRequest newOrder, OrderRequest existingOrder) {

        if (newOrder.getSide() == OrderRequest.Side.BUY) {
            return newOrder.getPrice() >= existingOrder.getPrice();
        } else {
            return newOrder.getPrice() <= existingOrder.getPrice();
        }
    }

    private double getExecutionPrice(OrderRequest o1, OrderRequest o2) {

        return o1.getCreatedAt().isBefore(o2.getCreatedAt())
                ? o1.getPrice()
                : o2.getPrice();
    }

    private void trade(OrderRequest o1, OrderRequest o2) {

        if(Objects.isNull(o2)){
            return;
        }
        double executionPrice = getExecutionPrice(o1, o2);
        log.info("Execution price is {}", executionPrice);

        OrderRequest buy  = o1.getSide() == OrderRequest.Side.BUY ? o1 : o2;
        OrderRequest sell = o1.getSide() == OrderRequest.Side.SELL ? o1 : o2;

        int tradedQty = Math.min(buy.getQuantity(), sell.getQuantity());
        log.info("Trading Qty is {}", tradedQty);

        orderRepository.updateOrderState(buy.getId(), OrderRequest.State.FILLED, String.format("Bought %d from %s for %f",tradedQty,sell.getTraderId(),executionPrice));
        orderRepository.updateOrderState(sell.getId(), OrderRequest.State.FILLED, String.format("Sold %d to %s for %f",tradedQty,buy.getTraderId(),executionPrice));
        sell.setQuantity(-tradedQty);
        sell.setPrice(executionPrice);
        portfolioRepository.addPortfolio(buy);

        portfolioRepository.addPortfolio(
               sell
        );
    }
    private OrderRequest getOrderToTrade(OrderRequest orderRequest) {

        return orderRepository.getAllPendingOrders().stream()

                .filter(o -> !o.getTraderId().equals(orderRequest.getTraderId()))

                .filter(o -> isMatchingSide(orderRequest, o))

                .filter(o -> isPriceMatch(orderRequest, o))

                .sorted(Comparator.comparing(OrderRequest::getCreatedAt)) // earliest first

                .findFirst()

                .orElse(null);
    }
    private boolean isMatchingSide(OrderRequest newOrder, OrderRequest existingOrder) {
        // If new is BUY, we need a SELL. If new is SELL, we need a BUY.
        return !newOrder.getSide().equals(existingOrder.getSide());
    }


    public String cancelOrder(long id) {
        log.info("Canceling order {}", id);
        OrderRequest order = orderRepository.getOrderById(id);
        if(!order.getState().equals(OrderRequest.State.PENDING)){
            return "ORDER CANNOT BE CANCELLED AS IT IS NOT IN PENDING STATE";
        }
        if(!order.getTraderId().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to cancel order");
        }

        orderRepository.updateOrderState(id, OrderRequest.State.CANCELLED, "USER REQUESTED CANCELLATION");
        log.info("Order {} has been cancelled", order);
        return "ORDER CANCELLED SUCCESSFULLY";
    }
}
