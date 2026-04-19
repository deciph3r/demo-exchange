package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.Portfolio;
import com.ahamed.demoexchange.model.SectorOverlapResponse;
import com.ahamed.demoexchange.model.User;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class PortfolioService {
    final PortfolioRepository portfolioRepository;
    public Portfolio getPortfolio(String username) {
        log.info("Getting portfolio for user {}", username);
        Portfolio portfolio = portfolioRepository.getUsersPortfolio(username);
        log.info("calculating sector holdings");
        Map<String, Integer> sectorMap = portfolio.getHoldings()
                .stream()
                .collect(Collectors.groupingBy(
                        Portfolio.Holding::getSector,
                        Collectors.summingInt(Portfolio.Holding::getQuantity)
                ));
        portfolio.setSectorHoldings(sectorMap);
        return portfolio;
    }
    public Portfolio addToPortfolio(OrderRequest orderRequest) {
        String traderId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        orderRequest.setTraderId(traderId);
        portfolioRepository.addPortfolio(orderRequest);

        return getPortfolio(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
     }

    public SectorOverlapResponse getSectorOverlap(String username) {

        Portfolio portfolio = getPortfolio(username);

        // Extract unique stocks from portfolio
        Set<String> portfolioStocks = portfolio.getHoldings()
                .stream()
                .map(Portfolio.Holding::getSymbol)
                .collect(Collectors.toSet());

        Map<String, Set<String>> baskets = Map.of(
                "TECH_HEAVY", Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA"),
                "FINANCE_HEAVY", Set.of("JPM", "GS", "BAC", "MS", "WFC"),
                "BALANCED", Set.of("AAPL", "JPM", "XOM", "JNJ", "TSLA")
        );

        List<SectorOverlapResponse.Overlap> overlapList = new ArrayList<>();

        double maxOverlap = 0;
        String dominantBasket = "";

        for (Map.Entry<String, Set<String>> entry : baskets.entrySet()) {

            String basketName = entry.getKey();
            Set<String> basketStocks = entry.getValue();

            // intersection
            long common = portfolioStocks.stream()
                    .filter(basketStocks::contains)
                    .count();

            double overlap = 0;
            if (!portfolioStocks.isEmpty()) {
                overlap = (2.0 * common / (portfolioStocks.size() + basketStocks.size())) * 100;
            }

            if (overlap > maxOverlap) {
                maxOverlap = overlap;
                dominantBasket = basketName;
            }

            overlapList.add(
                    new SectorOverlapResponse.Overlap(
                            basketName,
                            String.format("%.2f%%", overlap)
                    )
            );
        }

        String riskFlag;
        if (maxOverlap >= 60) {
            riskFlag = "HIGH";
        } else if (maxOverlap >= 40) {
            riskFlag = "MEDIUM";
        } else {
            riskFlag = "LOW";
        }

        return new SectorOverlapResponse(overlapList, dominantBasket, riskFlag);
    }

}
