package com.ahamed.demoexchange.service;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.Portfolio;
import com.ahamed.demoexchange.model.User;
import com.ahamed.demoexchange.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PortfolioService {
    final PortfolioRepository portfolioRepository;
    public Portfolio getPortfolio(String username) {
        Portfolio portfolio = portfolioRepository.getUsersPortfolio(username);

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
