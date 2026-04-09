package com.ahamed.demoexchange.model;

import lombok.Data;

import java.time.Instant;

@Data
public class OrderRequest {
    String traderId;
    String stock;
    int quantity;
    double takeProfit;
    double stopLoss;
    Side side;
    State state;
    Instant createdAt;
    Instant expiresAt;

    public enum Side {
        BUY, SELL
    }
    public enum State{
        PENDING, FILLED, CANCELLED
    }
}
