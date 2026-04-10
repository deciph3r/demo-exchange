package com.ahamed.demoexchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SectorOverlapResponse {
    private List<Overlap> overlaps;
    private String dominantBasket;
    private String riskFlag;

    @Data
    @AllArgsConstructor
    public static class Overlap {
        private String basket;
        private String overlap;
    }
}
