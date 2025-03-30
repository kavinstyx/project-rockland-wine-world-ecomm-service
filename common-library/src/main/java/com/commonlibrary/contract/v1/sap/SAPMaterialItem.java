package com.commonlibrary.contract.v1.sap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAPMaterialItem {

    @JsonProperty("NAVITEMNO")
    private String navItemNo;            // Navi Item Code (Length: 40)

    @JsonProperty("SAPITEMNO")
    private String sapItemNo;            // Material Number (Length: 40)

    @JsonProperty("DESCRIPTION")
    private String description;          // Material Description (Length: 40)

    @JsonProperty("SEARCHDESC")
    private String searchDesc;           // Material Description (Length: 40)

    @JsonProperty("DESCRIPTION2")
    private String description2;         // Additional Description (Length: 40)

    @JsonProperty("BASEUOM")
    private String baseUom;              // Base Unit of Measure (Length: 3)

    @JsonProperty("TYPE")
    private String type;                 // Type (Length: 10)

    @JsonProperty("INVPOSTGROUP")
    private String invPostGroup;         // Invoice Post Group (Length: 10)

    @JsonProperty("ALLOWINVDISC")
    private String allowInvDisc;         // Allow Inv Description (Length: 1)

    @JsonProperty("COSTINGMETHOD")
    private String costingMethod;        // Costing Method (Length: 10)

    @JsonProperty("VENDORNO")
    private String vendorNo;             // Vendor No (Length: 10)

    @JsonProperty("UNITVOLUME")
    private Double unitVolume;           // Volume (Length: 13)

    @JsonProperty("BLOCKED")
    private String blocked;              // Material Block / Cross plant (Length: 10)

    @JsonProperty("PRICESINCLUVAT")
    private String pricesIncluVat;       // Price Inclusive VAT (Length: 10)

    @JsonProperty("GENPRODPOSTGRP")
    private String genProdPostGrp;       // General Product Posting Group (Length: 10)

    @JsonProperty("VATPRODPOSTGRP")
    private String vatProdPostGrp;       // VAT Production Group (Length: 10)

    @JsonProperty("REPLNSHMNTSYSTM")
    private String replnshmntSystm;      // Replenishment System (Length: 10)

    @JsonProperty("ROUNDINGPRECISION")
    private String roundingPrecision;    // Rounding Precision (Length: 10)

    @JsonProperty("SALESUOM")
    private String salesUom;             // Sales Unit of Measure (Length: 3)

    @JsonProperty("PURCHUOM")
    private String purchUom;             // Purchase Unit of Measure (Length: 3)

    @JsonProperty("ITEMCATCODE")
    private String itemCatCode;          // Item Category / Material Group (Length: 60)

    @JsonProperty("PRODUCTGRPCODE")
    private String productGrpCode;       // Product Group Code (Length: 10)

    @JsonProperty("PRODUCTSUBGRP1")
    private String productSubGrp1;       // Product Sub Group 1 (Length: 10)

    @JsonProperty("PRODUCTSUBGRP2")
    private String productSubGrp2;       // Product Sub Group 2 (Length: 10)

    @JsonProperty("WEBREFERENCENO")
    private String webReferenceNo;       // Web Product Reference (Length: 18)

    @JsonProperty("WEBAVILBPERCNTAGE")
    private String webAvilbPercntage;    // Web Available Percentage (Length: 18)

}
