package rockland.elysiancrest.com.data_service.report.sales_analysis_sku_level;

import lombok.Data;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

@Data
public class SalesAnalysisSKUDto {
    @CsvHeader("SKU")
    private String sku;

    @CsvHeader("Date")
    private String date;

    @CsvHeader("Order Number")
    private Long orderNumber;

    @CsvHeader("Product Name")
    private String productName;

    @CsvHeader("Price (LKR)")
    private String price;

    @CsvHeader("Quantity")
    private int quantity;

    @CsvHeader("Total (LKR)")
    private String total;
}
