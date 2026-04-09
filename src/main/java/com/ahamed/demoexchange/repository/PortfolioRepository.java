package com.ahamed.demoexchange.repository;

import com.ahamed.demoexchange.model.Portfolio;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class PortfolioRepository {
    final JdbcTemplate jdbcTemplate;

    public Portfolio getUsersPortfolio(String userName) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserName(userName);
        List<Portfolio.Holding> holdings = jdbcTemplate.query("""
                SELECT s.symbol, p.quantity, s.sector
                FROM portfolio p
                INNER JOIN symbols s ON s.id = p.symbol_id
                INNER JOIN users u ON u.id = p.user_id
                WHERE u.username = ?;
                """, (rs, _) -> {
            Portfolio.Holding holding = new Portfolio.Holding();
            holding.setSymbol(rs.getString("symbol"));
            holding.setQuantity(rs.getInt("quantity"));
            holding.setSector(rs.getString("sector"));
            return holding;
        }, userName);

        portfolio.setHoldings(holdings);
        return portfolio;
    }

    public Integer getUserHolding(String userName, String symbol) {
        return jdbcTemplate.queryForObject("SELECT quantity FROM portfolio WHERE user_id = (SELECT id from users where username = ? ) AND symbol_id = (SELECT id from symbols where symbol= ?)", Integer.class, userName, symbol);
    }
}
