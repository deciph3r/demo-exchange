package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.Portfolio;
import com.ahamed.demoexchange.model.User;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
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
       portfolioRepository.addPortfolio(orderRequest);

        return getPortfolio(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
     }

}
