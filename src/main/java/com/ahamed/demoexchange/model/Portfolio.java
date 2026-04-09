package com.ahamed.demoexchange.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Portfolio {
    String userName;
    List<Holding> holdings;
    Map<String, Integer> sectorHoldings;
    @Data
    public static class Holding{
        String symbol;
        int quantity;
        String sector;
    }
}
