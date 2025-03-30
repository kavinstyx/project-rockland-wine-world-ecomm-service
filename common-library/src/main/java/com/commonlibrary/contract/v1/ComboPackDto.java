package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboPackDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal totalPrice;

    // Product IDs for POST/PUT requests
    private List<Long> productIds;

    // Product details for GET requests
    private List<Map<String, String>> productList;
}
