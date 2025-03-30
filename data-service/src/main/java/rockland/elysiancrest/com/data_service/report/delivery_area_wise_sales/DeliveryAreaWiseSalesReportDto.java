package rockland.elysiancrest.com.data_service.report.delivery_area_wise_sales;

import lombok.Data;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

@Data
public class DeliveryAreaWiseSalesReportDto {
    @CsvHeader("Delivery City")
    private String deliveryCity;

    @CsvHeader("Order Count")
    private int orderCount;

    @CsvHeader("Gross Sale (LKR)")
    private String grossSale;

    @CsvHeader("Delivery Fee (LKR)")
    private String deliveryFee;

    @CsvHeader("Net Sale (LKR)")
    private String netSale;
}
