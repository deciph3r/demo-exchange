package com.ahamed.demoexchange.repository;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
@AllArgsConstructor
public class OrderRepository {
    final JdbcTemplate jdbcTemplate;
    final SymbolRepository symbolRepository;
    public long placeOrder(OrderRequest orderRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO orders (
                  trader_id, stock_id, quantity,
                  take_profit, stop_loss, side, state, expires_at,created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, user.getId());
            ps.setLong(2, symbolRepository.getStockId(orderRequest.getStock())); // store stock as its ID (string)
            ps.setInt(3, orderRequest.getQuantity());
            ps.setDouble(4, orderRequest.getTakeProfit());
            ps.setDouble(5, orderRequest.getStopLoss());
            // store enums as their names
            ps.setString(6, orderRequest.getSide() != null ? orderRequest.getSide().name() : null);
            ps.setString(7, orderRequest.getState() != null ? orderRequest.getState().name() : null);
            // expiresAt stored as epoch millis (BIGINT)
            ps.setLong(8, orderRequest.getExpiresAt() != null ? orderRequest.getExpiresAt().toEpochMilli() : 0L);

            // createdAt stored as epoch millis (BIGINT)
            ps.setLong(9, orderRequest.getCreatedAt() != null ? orderRequest.getCreatedAt().toEpochMilli() : 0L);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<OrderRequest> getAllPendingOrderForTrader(String traderName) {
        String sql = """
                SELECT s.symbol, o.quantity, o.side
                FROM orders o
                INNER JOIN symbols s ON s.id = o.stock_id
                INNER JOIN users u ON u.id = o.trader_id
                WHERE u.username = ?
                AND o.state = 'PENDING'
                """;

        return jdbcTemplate.query(sql, (rs, _) -> {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setStock(rs.getString("symbol"));
            orderRequest.setQuantity(rs.getInt("quantity"));
            orderRequest.setSide(OrderRequest.Side.valueOf(rs.getString("side")));
            return orderRequest;
        }, traderName);
    }

    public OrderRequest getOrderById(long id) {
        String sql = """
                SELECT o.state, u.username from orders o
                inner join users u on u.id = o.trader_id
                where o.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, _) -> {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setState(OrderRequest.State.valueOf(rs.getString("state")));
            orderRequest.setTraderId(rs.getString("username"));
            return orderRequest;
        }, id);

    }

    public void updateOrderState(long id, OrderRequest.State newState){
        String sql = "UPDATE orders SET state = ? WHERE id = ?";
        jdbcTemplate.update(sql, newState.name(), id);
    }
}
