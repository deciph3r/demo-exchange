package com.ahamed.demoexchange.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
@AllArgsConstructor
public class SymbolRepository {
    final JdbcTemplate jdbcTemplate;

    public long getStockId (String symbol){
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM symbols WHERE symbol = ?", Long.class, symbol);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid stock symbol: " + symbol);
        }
    }
}
