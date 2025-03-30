package rockland.elysiancrest.com.data_service.report.order_summary_report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryReportDto {
    @CsvHeader("Order Date")
    private String orderDate;

    @CsvHeader("Order Number")
    private Long orderNumber;

    @CsvHeader("Customer Name")
    private String customerName;

    @CsvHeader("Item Sub Total (LKR)")
    private String itemSubTotal;

    @CsvHeader("Delivery Fee (LKR)")
    private String deliveryFee;

    @CsvHeader("Total (LKR)")
    private String total;

    @CsvHeader("Delivery Date")
    private Date deliveryDate;

    @CsvHeader("Status")
    private String status;
}
