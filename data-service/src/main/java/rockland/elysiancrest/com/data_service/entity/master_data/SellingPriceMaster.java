package rockland.elysiancrest.com.data_service.entity.master_data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDate;

@Entity
@Table(name = "selling_price_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class SellingPriceMaster extends BaseEntity {

    @Column(name = "sap_material_code", nullable = false)
    private String sapMaterialCode;

    @Column(name = "nav_item_no")
    private String navItemNo;

    @Column(name = "sales_code")
    private String salesCode;

    @Column(name = "sales_type")
    private String salesType;

    @Column(name = "customer_group_type")
    private String customerGroupType;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    //unit Price including Vat
    @Column(name = "unit_price_incl_vat")
    private Double unitPriceInclVat;

    //unit Price
    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "variant_code")
    private String variantCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "minimum_quantity")
    private Integer minimumQuantity;

    @Column(name = "prices_incl_vat")
    private Double pricesInclVat;

    @Column(name = "allow_inv_disc")
    private String allowInvDisc;

    @Column(name = "vat_bus_post_grp_pric", length = 10)
    private Integer vatBusPostGrpPric;

    @Column(name = "allow_line_disc")
    private String allowLineDisc;



}
