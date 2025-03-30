package rockland.elysiancrest.com.data_service.report.sales_analysis_order_level;

import lombok.Builder;
import lombok.Data;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

@Data
@Builder
public class SalesAnalysisOrderLevelDto {
    @CsvHeader("Date")
    private String date;

    @CsvHeader("Day")
    private String day;

    @CsvHeader("Order Count")
    private Integer orderCount;

    @CsvHeader("Gross Sale (LKR)")
    private String grossSale;

    @CsvHeader("Delivery Fee (LKR)")
    private String deliveryFee;

    @CsvHeader("Net Sale (LKR)")
    private String netSale;

    @CsvHeader("Average Basket Value (Gross - LKR)")
    private String averageBasketValueGross;

    @CsvHeader("Average Basket Value (Net - LKR)")
    private String averageBasketValueNet;
}
