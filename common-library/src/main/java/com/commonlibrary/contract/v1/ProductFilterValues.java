package com.commonlibrary.contract.v1;

import lombok.Data;

import java.util.List;

@Data
public class ProductFilterValues {
    private Long departmertId;
    private String department;
    private List<CatrgoryVlaues> categories;
    private Double minPriceInDollar;
    private Double maxPriceInDollar;
    private Double minPriceInRupee;
    private Double maxPriceInRupee;

    @Data
    public class CatrgoryVlaues{
        private Long categoryId;
        private String catrgory;
        private List<Brandvalues> brands;
    }

    @Data
    public static class Brandvalues{
        private Long brandId;
        private String brand;
    }
}
