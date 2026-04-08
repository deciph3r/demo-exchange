package com.ahamed.demoexchange.repository;

import com.ahamed.demoexchange.model.OrderRequest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@AllArgsConstructor
public class OrderRepository {
    final JdbcTemplate jdbcTemplate;

    public long placeOrder(OrderRequest orderRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO orders (
                  trader_id, stock, sector, quantity,
                  take_profit, stop_loss, side, state, expires_at,created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderRequest.getTraderId());
            ps.setString(2, orderRequest.getStock());
            ps.setString(3, orderRequest.getSector());
            ps.setInt(4, orderRequest.getQuantity());
            ps.setDouble(5, orderRequest.getTakeProfit());
            ps.setDouble(6, orderRequest.getStopLoss());
            // store enums as their names
            ps.setString(7, orderRequest.getSide() != null ? orderRequest.getSide().name() : null);
            ps.setString(8, orderRequest.getState() != null ? orderRequest.getState().name() : null);
            // expiresAt stored as epoch millis (BIGINT)
            ps.setLong(9, orderRequest.getExpiresAt() != null ? orderRequest.getExpiresAt().toEpochMilli() : 0L);

            // createdAt stored as epoch millis (BIGINT)
            ps.setLong(10, orderRequest.getCreatedAt() != null ? orderRequest.getCreatedAt().toEpochMilli() : 0L);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
