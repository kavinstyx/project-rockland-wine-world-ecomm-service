package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String material;
    private String navItemCode;
    private String urlCode;
    private String wwProductRef;
    private String sku;
    private String materialType;
    private String newName;
    private String correctName;
    private BigDecimal regularPriceInRupee;
    private BigDecimal regularPriceInDollar;
    private String materialDescription;
    private String baseUnitOfMeasure;
    private String countryOfOrigin;
    private Double alcoholStrength;
    private BigDecimal volume;
    private String volumeUnit;
    private BigDecimal volumePerPerson;
    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private String weightUnit;
    private String sapCategory;
    private String sapDepartment;
    private String materialGroup;
    private String materialGroup1;
    private String materialGroup2;
    private String materialGroup3;
    private String materialGroup4;
    private String materialGroup5;
    private String manufactuer;
    private String thumbnailUrl;
    private String imageUrl;
    private String imageUrl2;
    private String imageUrl3;
    private String imageUrl4;
    private String imageUrl5;
    private Boolean isContinue;
    private Boolean isFastMoving;
    private Boolean isEnabled;
    private Boolean isWebSellingEnabled;
    private Boolean isFeatureProduct;
    private Boolean isBestSeller;
    private Boolean isNewArrival;
    private String brandName;


    private Long brandId; // ID for BrandMaster
    private Long categoryId; // ID for CategoryMaster
    private String categoryName;
    private Long departmentId;
    private String departmentName;

    private List<Long> comboPackIds;

    private boolean isFavorite;
    private int quantity;
}
