package rockland.elysiancrest.com.data_service.report.extended_sales;

import lombok.Data;
import rockland.elysiancrest.com.data_service.enums.CsvHeader;

@Data
public class ExtendedSalesReportDto {

    @CsvHeader("Order No")
    private String orderNo;

    @CsvHeader("Order Date")
    private String orderDate;

    @CsvHeader("Order Time")
    private String orderTime;

    @CsvHeader("SKU Code")
    private String skuCode;

    @CsvHeader("Product Name")
    private String productName;

    @CsvHeader("ERP Code")
    private String erpCode;

    @CsvHeader("Department")
    private String department;

    @CsvHeader("Category")
    private String category;

    @CsvHeader("Sub Category")
    private String subCategory;

    @CsvHeader("Vendor")
    private String vendor;

    @CsvHeader("Brand")
    private String brand;

    @CsvHeader("Base Price")
    private String basePrice;

    @CsvHeader("Applied Discount")
    private String appliedDiscount;

    @CsvHeader("Actual Price")
    private String actualPrice;

    @CsvHeader("Quantity")
    private int quantity;

    @CsvHeader("Gross Sale")
    private String grossSale;

    @CsvHeader("Net Sale")
    private String netSale;

    @CsvHeader("Delivery Charges")
    private String deliveryCharges;

    @CsvHeader("Grand Total")
    private String grandTotal;

    @CsvHeader("Processing Fee")
    private String processingFee;

    @CsvHeader("Total Payment Received")
    private String totalPaymentReceived;

    @CsvHeader("Due Amount")
    private String dueAmount;

    @CsvHeader("Payment Method")
    private String paymentMethod;

    @CsvHeader("Sourcing Branch")
    private String sourcingBranch;

    @CsvHeader("Packaging Name")
    private String packagingName;

    @CsvHeader("Packaging Price")
    private String packagingPrice;

    @CsvHeader("Warranty Name")
    private String warrantyName;

    @CsvHeader("Warranty Price")
    private String warrantyPrice;

    @CsvHeader("Order Branch")
    private String orderBranch;

    @CsvHeader("Order Placed By")
    private String orderPlacedBy;

    @CsvHeader("Delivery Date")
    private String deliveryDate;

    @CsvHeader("Delivery Time Range")
    private String deliveryTimeRange;

    @CsvHeader("Customer Name")
    private String customerName;

    @CsvHeader("Customer Email")
    private String customerEmail;

    @CsvHeader("User Type")
    private String userType;

    @CsvHeader("User ID")
    private String userId;

    @CsvHeader("Contact Number 1")
    private String contactNumber1;

    @CsvHeader("Contact Number 2")
    private String contactNumber2;

    @CsvHeader("Receiver Name")
    private String receiverName;

    @CsvHeader("Receiver Address")
    private String receiverAddress;

    @CsvHeader("Receiver Contact 1")
    private String receiverContact1;

    @CsvHeader("Receiver Contact 2")
    private String receiverContact2;

    @CsvHeader("City")
    private String city;

    @CsvHeader("Payment Status")
    private String paymentStatus;

    @CsvHeader("Payment Type")
    private String paymentType;

    @CsvHeader("Payment ID")
    private String paymentId;

    @CsvHeader("Payment Amount Received")
    private String paymentAmountReceived;

    @CsvHeader("Payment Received Date")
    private String paymentReceivedDate;

    @CsvHeader("Payment Received Time")
    private String paymentReceivedTime;

    @CsvHeader("Service Payment Status")
    private String servicePaymentStatus;

    @CsvHeader("Service Payment Ref")
    private String servicePaymentRef;

    @CsvHeader("Service Payment Received")
    private String servicePaymentReceived;

    @CsvHeader("Service Payment Received Date")
    private String servicePaymentReceivedDate;

    @CsvHeader("Service Payment Received Time")
    private String servicePaymentReceivedTime;

    @CsvHeader("Promotion")
    private String promotion;

    @CsvHeader("Payment proceeded MID")
    private String paymentProceededMID;

    @CsvHeader("Item Status")
    private String itemStatus;

    @CsvHeader("Added/Removed Value")
    private String addedRemovedValue;

    @CsvHeader("Auth Code")
    private String authCode;

    @CsvHeader("Card Number")
    private String cardNumber;

    @CsvHeader("Captured Amount")
    private String capturedAmount;

    @CsvHeader("Captured Date")
    private String capturedDate;

    @CsvHeader("Order Status")
    private String orderStatus;

    @CsvHeader("BatchCode")
    private String batchCode;

    @CsvHeader("BatchSerial")
    private String batchSerial;

    @CsvHeader("NIC")
    private String nic;

    @CsvHeader("Special Instructions")
    private String specialInstructions;

    @CsvHeader("Location Type")
    private String locationType;

    @CsvHeader("Location Comment")
    private String locationComment;

    @CsvHeader("Delivery Medium")
    private String deliveryMedium;

    @CsvHeader("Refundable Amount")
    private String refundableAmount;

    @CsvHeader("Refunded Amount")
    private String refundedAmount;

    @CsvHeader("Refunded By")
    private String refundedBy;

    @CsvHeader("Refund Ref No")
    private String refundRefNo;

    @CsvHeader("Delivery Type")
    private String deliveryType;

    @CsvHeader("Is Samsung Order")
    private boolean isSamsungOrder;

    @CsvHeader("IP Address")
    private String ipAddress;

    @CsvHeader("Country")
    private String country;

    @CsvHeader("Order Verified Date & Time Stamp")
    private String orderVerifiedDateTimeStamp;

    @CsvHeader("Order Approved Date & Time Stamp")
    private String orderApprovedDateTimeStamp;

    @CsvHeader("Comment 1")
    private String comment1;

    @CsvHeader("Comment 2")
    private String comment2;

    @CsvHeader("Comment 3")
    private String comment3;
}
