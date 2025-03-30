package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellingPrice {
    private Long id;
    private String sapMaterialCode;
    private String navItemNo;
    private String salesCode;
    private Boolean salesType;
    private String customerGroupType;
    private String unitOfMeasure;
    private Double unitPriceInclVat;
    private Double unitPrice;
    private String currency;
    private String variantCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Integer minimumQuantity;
    private Boolean pricesInclVat;
    private Boolean allowInvDisc;
    private String vatBusPostGrpPric;
    private Boolean allowLineDisc;
}
