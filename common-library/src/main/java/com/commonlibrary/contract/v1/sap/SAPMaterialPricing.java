package com.commonlibrary.contract.v1.sap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAPMaterialPricing {

    @JsonProperty("NAVITEMNO")
    private String navItemNo;            // Navi Item Code

    @JsonProperty("SAPITEMNO") 
    private String sapItemNo;            // Material Number

    @JsonProperty("STARTINGDATE")
    private LocalDate startingDate;      // Valid-From Date

    @JsonProperty("ENDINGDATE")
    private LocalDate endingDate;        // Valid-To Date

    @JsonProperty("SALESTYPE")
    private String salesType;            // Sales Type

    @JsonProperty("SALESCODE")
    private String salesCode;            // Sales Code

    @JsonProperty("CURRENCYCODE")
    private String currencyCode;         // Currency Key

    @JsonProperty("VARIANTCODE")
    private String variantCode;          // Variant Code

    @JsonProperty("UOMCODE")
    private String uomCode;              // Base Unit of Measure

    @JsonProperty("UNITPRICE")
    private Double unitPrice;            // Unit Price

    @JsonProperty("PRICESINCLUVAT")
    private Double pricesIncluVat;       // Price include VAT

    @JsonProperty("ALLOWINVDISC")
    private String allowInvDisc;         // Allow Invoice Discount

    @JsonProperty("VATBUSPOSTGRPPRIC")
    private Integer vatBusPostGrpPric;   // Group Pricing

    @JsonProperty("ALLOWLINEDISC")
    private String allowLineDisc;        // Allow Line Discount

    @JsonProperty("UNITPRICEINCLVAT")
    private Double unitPriceInclVat;     // Unit Price Including VAT
}
