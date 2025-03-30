package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    private Long id;
    private String brandName;
    private List<Product> products;
}
