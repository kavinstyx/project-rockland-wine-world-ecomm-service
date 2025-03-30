package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMasterDTO {

    private Long id;
    private String sapMaterialCode;
    private String navItemCode;
    private String urlCode;
    private String sku;
    private String type;
    private String newName;
    private String correctName;
    private BigDecimal regularPriceInRupee;
    private BigDecimal regularPriceInDollar;
    private String description;
    private String unitOfMeasure;
    private String countryOfOrigin;
    private Double alcoholStrength;
    private Integer bottleSizeMl;
    private Double weightGrams;
    private String sapCategory;
    private String sapDepartment;
    private Boolean isEnabled;
    private Boolean isWebSellingEnabled;
    private Boolean isFeatureProduct;
    private Boolean isBestSeller;
    private Boolean isNewArrival;
    private String imageUrl;

    private Long brandId; // ID for BrandMaster
    private Long categoryId; // ID for CategoryMaster
    private Long departmentId; // ID for DepartmentMaster
}
