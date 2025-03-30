package rockland.elysiancrest.com.data_service.dto.dashboard;

import lombok.Data;

import java.util.List;

@Data
public class OrderDataDTO {
    private Double totalSale;
    private Double deliveryCost; 
    private Double netSales;
    private Integer numberOfOrders;
    private Double averageOrderValue;
    private Double averageSkusPerOrder;

    private Double totalSaleTarget;
    private Double deliveryCostTarget;
    private Double netSalesTarget;
    private Integer numberOfOrdersTarget;
    private Double averageOrderValueTarget;
    private Double averageSkusPerOrderTarget;

    private List<OrderProductDataDTO> topProductsSold;
    private List<OrderCategoryDataDTO> topCategoriesSold;
}

