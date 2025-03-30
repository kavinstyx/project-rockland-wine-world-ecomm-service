package rockland.elysiancrest.com.data_service.report.sku_sales;

import lombok.Data;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

@Data
public class SKUSaleReportDto {
    @CsvHeader("SKU Code")
    private String skuCode;

    @CsvHeader("Product Name")
    private String productName;

    @CsvHeader("Number of Items")
    private int quantity;

    @CsvHeader("Total Sale (LKR)")
    private String totalPriceInRupee;
}
