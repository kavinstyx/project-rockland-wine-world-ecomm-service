package rockland.elysiancrest.com.data_service.entity.master_data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class ProductMaster extends BaseEntity {

    @Column(name = "Material", unique = true, length = 20)
    private String material;

    @Column(name = "url_code", unique = true, length = 100)
    private String urlCode;

    @Column(name = "nav_item_code", unique = true)
    private String navItemCode;

    @Column(name = "WW_Product_Ref", length = 20)
    private String wwProductRef;

    @Column( unique = true)
    private String sku;

    @Column(name = "Material_type", length = 10)
    private String materialType;

    @Column(name = "new_name")
    private String newName;

    @Column(name = "correct_name")
    private String correctName;

    @Column(name = "regular_price_rupee", precision = 10, scale = 2)
    private BigDecimal regularPriceInRupee;

    @Column(name = "regular_price_dollar", precision = 10, scale = 2)
    private BigDecimal regularPriceInDollar;

    @Column(name = "Material_Description", length = 1500)
    private String materialDescription;

    @Column(name = "material_Long_Description", length = 2500)
    private String materialLongDescription;

    @Column(name = "Base_Unit_of_Measure", length = 10)
    private String baseUnitOfMeasure;

    @Column(name = "Country_of_Origin", length = 20)
    private String countryOfOrigin;

    @Column(name = "Strength", precision = 4, scale = 2)
    private BigDecimal alcoholStrength;

    @Column(name = "Volume", precision = 4, scale = 3)
    private BigDecimal volume;

    @Column(name = "Volume_Unit", length = 10)  // varchar(10)
    private String volumeUnit;

    @Column(name = "volume_per_person", precision = 4, scale = 3)
    private BigDecimal volumePerPerson;

    @Column(name = "Gross_weight", precision = 4, scale = 3)
    private BigDecimal grossWeight;

    @Column(name = "Net_weight", precision = 4, scale = 3)
    private BigDecimal netWeight;

    @Column(name = "Weight_unit", length = 10)
    private String weightUnit;

    @Column(name = "sap_category")
    private String sapCategory;

    @Column(name = "sap_department")
    private String sapDepartment;

    @Column(name = "Material_Group", length = 10)
    private String materialGroup;

    @Column(name = "Material_Group_1", length = 10)  // varchar(10)
    private String materialGroup1;

    @Column(name = "Material_Group_2", length = 10)  // varchar(10)
    private String materialGroup2;

    @Column(name = "Material_Group_3", length = 10)  // varchar(10)
    private String materialGroup3;

    @Column(name = "Material_Group_4", length = 10)  // varchar(10)
    private String materialGroup4;

    @Column(name = "Material_Group_5", length = 10)  // varchar(10)
    private String materialGroup5;

    @Column(name = "Manufactuer", length = 20)
    private String manufactuer;

    @Column(name = "sap_manufacturer", length = 100)
    private String sapManufacturer;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    @Column(name = "image_url_4")
    private String imageUrl4;

    @Column(name = "image_url_5")
    private String imageUrl5;

    @Column(name = "is_continue")
    private Boolean isContinue =true;

    @Column(name = "is_fast_moving")
    private Boolean isFastMoving = true;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true; //enable for sales

    @Column(name = "is_web_selling_enabled")
    private Boolean isWebSellingEnabled = true;

    @Column(name = "is_feature_product")
    private Boolean isFeatureProduct = true;

    @Column(name = "is_best_seller")
    private Boolean isBestSeller = true;

    @Column(name = "is_new_arrival")
    private Boolean isNewArrival = true;

    @Column(name = "Brand_Name", length = 20)
    private String brandName;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandMaster brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryMaster category;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentMaster department;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Inventory> inventory;

    @Column(name = "combo_pack_details", columnDefinition = "TEXT")
    private String comboPackDetails;


}
