package com.ahamed.demoexchange.repository;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class OrderRepository {
    final JdbcTemplate jdbcTemplate;
    final SymbolRepository symbolRepository;
    public OrderRequest placeOrder(OrderRequest orderRequest) {
        log.info("Placing order {}", orderRequest);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO orders (
                  trader_id, stock_id, quantity,
                  price, side, state, expires_at,created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Inserting to orders table");
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, user.getId());
            ps.setLong(2, symbolRepository.getStockId(orderRequest.getStock())); // store stock as its ID (string)
            ps.setInt(3, orderRequest.getQuantity());
            ps.setDouble(4, orderRequest.getPrice());
            // store enums as their names
            ps.setString(5, orderRequest.getSide() != null ? orderRequest.getSide().name() : null);
            ps.setString(6, orderRequest.getState() != null ? orderRequest.getState().name() : null);
            // expiresAt stored as epoch millis (BIGINT)
            ps.setLong(7, orderRequest.getExpiresAt() != null ? orderRequest.getExpiresAt().getEpochSecond() : 0L);

            // createdAt stored as epoch millis (BIGINT)
            ps.setLong(8, orderRequest.getCreatedAt() != null ? orderRequest.getCreatedAt().getEpochSecond() : 0L);
            return ps;
        }, keyHolder);
        orderRequest.setId(keyHolder.getKey().longValue());
        log.info("Inserted order {}", orderRequest);
        return orderRequest;
    }

    public List<OrderRequest> getAllPendingOrderForTrader(String traderName) {

        log.info("Getting orders for trader {}", traderName);
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
        log.info("Getting order by id {}", id);
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

    public void updateOrderState(long id, OrderRequest.State newState,String remarks){
        log.info("Updating order state {} for id {}", newState, id);
        String sql = "UPDATE orders SET state = ?, remarks= ? WHERE id = ?";
        jdbcTemplate.update(sql, newState.name(), remarks , id);
        log.info("Updated order state {} for id {} with remarks:{}", newState,id, remarks);
    }


    public List<OrderRequest> getAllPendingOrders(){
        log.info("Getting orders for all pending orders");
        String sql = """
                  SELECT s.symbol, o.quantity, o.side, u.username, o.id, o.price,o.created_at ,o.expires_at
                FROM orders o
                INNER JOIN symbols s ON s.id = o.stock_id
                INNER JOIN users u ON u.id = o.trader_id
                AND o.state = 'PENDING'
                """;
        return jdbcTemplate.query(sql, (rs, _) -> {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setId(rs.getLong("id"));
            orderRequest.setTraderId(rs.getString("username"));
            orderRequest.setPrice(rs.getDouble("price"));
            orderRequest.setStock(rs.getString("symbol"));
            orderRequest.setQuantity(rs.getInt("quantity"));
            orderRequest.setCreatedAt(java.time.Instant.ofEpochSecond(rs.getLong("created_at")));
            orderRequest.setExpiresAt(java.time.Instant.ofEpochSecond(rs.getLong("expires_at")));
            orderRequest.setSide(OrderRequest.Side.valueOf(rs.getString("side")));
            return orderRequest;
        });
    }
}
