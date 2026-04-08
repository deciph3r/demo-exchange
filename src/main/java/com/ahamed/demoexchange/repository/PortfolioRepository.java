package com.ahamed.demoexchange.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PortfolioRepository {
    final JdbcTemplate jdbcTemplate;

    public void getUsersPortfolio(String userName) {
        jdbcTemplate.queryForList("SELECT * FROM portfolio WHERE user_id = ?", userName);
    }

    public Integer getUserHolding(String userName, String symbol) {
        return jdbcTemplate.queryForObject("SELECT quantity FROM portfolio WHERE user_id = (SELECT id from users where username = ? ) AND symbol_id = (SELECT id from symbols where symbol= ?)", Integer.class, userName, symbol);
    }
}
