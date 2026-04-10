package com.ahamed.demoexchange.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
@AllArgsConstructor
@Slf4j
public class SymbolRepository {
    final JdbcTemplate jdbcTemplate;

    public long getStockId (String symbol){
        try {
            log.info("Getting stock id for {}", symbol);
            Long stockId = jdbcTemplate.queryForObject("SELECT id FROM symbols WHERE symbol = ?", Long.class, symbol);
            log.info("Found stock id for {} is {}", symbol, stockId);
            return stockId;
        } catch (DataAccessException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid stock symbol: " + symbol);
        }
    }
}
