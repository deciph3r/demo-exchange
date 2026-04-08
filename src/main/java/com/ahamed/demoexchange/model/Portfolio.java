package com.ahamed.demoexchange.model;

import lombok.Data;

import java.util.List;

@Data
public class Portfolio {
    String userName;
    List<Holding> holdings;

    static class Holding{
        String symbol;
        int quantity;
    }
}
