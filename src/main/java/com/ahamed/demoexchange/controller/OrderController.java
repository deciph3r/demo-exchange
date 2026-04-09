package com.ahamed.demoexchange.controller;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.model.Portfolio;
import com.ahamed.demoexchange.model.User;
import com.ahamed.demoexchange.service.OrderService;
import com.ahamed.demoexchange.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class OrderController {
    final OrderService orderService;
    final PortfolioService portfolioService;
    @PostMapping("/placeOrder")
    public long placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @PostMapping("/cancelOrder")
    public ResponseEntity<String> cancelOrder(@RequestParam long id) {
        return ResponseEntity.accepted().body(orderService.cancelOrder(id));
    }

    @GetMapping("/getPortfolio")
    public ResponseEntity<Portfolio> getPortfolio(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(portfolioService.getPortfolio(user.getUsername()));
    }

    @PostMapping("/addToPortfolio")
    public ResponseEntity<Portfolio> addToPortfolio(@RequestBody OrderRequest orderRequest) {
            return ResponseEntity.ok().body(portfolioService.addToPortfolio(orderRequest));
    }
}
