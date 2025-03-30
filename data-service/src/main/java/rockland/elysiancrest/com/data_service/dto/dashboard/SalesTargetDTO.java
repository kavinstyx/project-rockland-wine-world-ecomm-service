package rockland.elysiancrest.com.data_service.dto.dashboard;

import lombok.Data;

@Data
public class SalesTargetDTO {
    private Long id;
    private Integer year;
    private Integer month;
    private Double totalSale;
    private Double deliveryCost;
    private Double netSales;
    private Integer numberOfOrders;
    private Double averageOrderValue;
    private Double averageSkusPerOrder;
}
