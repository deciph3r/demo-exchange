package com.ahamed.demoexchange.controller;

import com.ahamed.demoexchange.model.OrderRequest;
import com.ahamed.demoexchange.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
public class OrderController {
    final OrderService orderService;
    @PostMapping("/placeOrder")
    public long placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }
}
