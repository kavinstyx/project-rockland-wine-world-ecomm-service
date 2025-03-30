package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCharges {
    private Long id;
    private String deliveryType;
    private Double basePriceInRupee;
    private Double twoBottlesPriceInRupee;
    private Double threeBottlesPriceInRupee;
    private Double fourBottlesPriceInRupee;
    private Double fiveBottlesPriceInRupee;
    private Double sixBottlesPriceInRupee;
    private Double sevenBottlesPriceInRupee;
    private Double eightBottlesPriceInRupee;
    private Double basePriceInDollar;
    private Double twoBottlesPriceInDollar;
    private Double threeBottlesPriceInDollar;
    private Double fourBottlesPriceInDollar;
    private Double fiveBottlesPriceInDollar;
    private Double sixBottlesPriceInDollar;
    private Double sevenBottlesPriceInDollar;
    private Double eightBottlesPriceInDollar;
    private Long cityId;
}
