package rockland.elysiancrest.com.data_service.report.extended_sales;

import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.FilterOptionDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderStatus;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExtendedSalesReport implements ReportSpecification<ExtendedSalesReportDto> {

    private static final String ONLINE = "online";
    private static final String EVENT_ORDER_TYPE = "eventOrderType";

    private final OrderRepo orderRepo;

    @Override
    public String getReportName() {
        return "Extended-Sales-Report";
    }

    @Override
    public Page<ExtendedSalesReportDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<OrderMaster> spec = createSpecification(filters);

        Page<OrderMaster> allOrders = orderRepo.findAll(spec, pageable);

        List<ExtendedSalesReportDto> dtos = allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream().map(orderItem -> {
                    BigDecimal itemSubTotal = orderItem.getUnitPriceInRupee().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(order.getOrderDate()));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = dateTime.format(formatter);

                    var dto = new ExtendedSalesReportDto();


                    return dto;
                }))
                .toList();

        // Wrap the DTO list into a pageable structure
        return new PageImpl<>(dtos, pageable, allOrders.getTotalElements());
    }

    @Override
    public List<ExtendedSalesReportDto> getFilteredData(List<SelectedFilterDto> filters) {
        Specification<OrderMaster> spec = createSpecification(filters);

        List<OrderMaster> allOrders = orderRepo.findAll(spec);

        return allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream().map(orderItem -> {
                    BigDecimal itemSubTotal = orderItem.getUnitPriceInRupee().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(order.getOrderDate()));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = dateTime.format(formatter);

                    var dto = new ExtendedSalesReportDto();


                    return dto;
                }))
                .toList();
    }

    @Override
    public List<FilterDto> getFilterOptions() {
        List<FilterDto> filterOptions = new ArrayList<>();
        List<OrderRepo.CustomerNameAndUserId> customerData = orderRepo.findAllCustomerNamesAndUserIds();
        List<FilterOptionDto> customerNameOptions = customerData.stream()
                .map(data -> new FilterOptionDto(String.valueOf(data.userId()), data.customerName()))
                .distinct()
                .toList();
        filterOptions.add(new FilterDto("Customer Name", "customerName", "multi-select", customerNameOptions, "Select Customer Name"));


        filterOptions.add(new FilterDto("Order Date", "orderDate", "date-range", null, "Select order date"));

        OrderStatus[] orderStatuses = OrderStatus.values();
        List<FilterOptionDto> statusOptions = Arrays.stream(orderStatuses)
                .map(status -> new FilterOptionDto(status.name(), status.name()))
                .toList();
        filterOptions.add(new FilterDto("Status", "status", "multi-select", statusOptions, "Select Status"));

        return filterOptions;
    }

    @Override
    public Class<ExtendedSalesReportDto> getType() {
        return ExtendedSalesReportDto.class;
    }

//    @Override
//    public List<String> getHeaders() {
//        return List.of(
//                "Order No",
//                "Order Date",
//                "Order Time",
//                "SKU Code",
//                "Product Name",
//                "ERP Code",
//                "Department",
//                "Category",
//                "Sub Category",
//                "Vendor",
//                "Brand",
//                "Base Price",
//                "Applied Discount",
//                "Actual Price",
//                "Quantity",
//                "Gross Sale",
//                "Net Sale",
//                "Delivery Charges",
//                "Grand Total",
//                "Processing Fee",
//                "Total Payment Received",
//                "Due Amount",
//                "Payment Method",
//                "Sourcing Branch",
//                "Packaging Name",
//                "Packaging Price",
//                "Warranty Name",
//                "Warranty Price",
//                "Order Branch",
//                "Order Placed By",
//                "Delivery Date",
//                "Delivery Time Range",
//                "Customer Name",
//                "Customer Email",
//                "User Type",
//                "User ID",
//                "Contact Number 1",
//                "Contact Number 2",
//                "Receiver Name",
//                "Receiver Address",
//                "Receiver Contact 1",
//                "Receiver Contact 2",
//                "City",
//                "Payment Status",
//                "Payment Type",
//                "Payment ID",
//                "Payment Amount Received",
//                "Payment Received Date",
//                "Payment Received Time",
//                "Service Payment Status",
//                "Service Payment Ref",
//                "Service Payment Received",
//                "Service Payment Received Date",
//                "Service Payment Received Time",
//                "Promotion",
//                "Payment proceeded MID",
//                "Item Status",
//                "Added/Removed Value",
//                "Auth Code",
//                "Card Number",
//                "Captured Amount",
//                "Captured Date",
//                "Order Status",
//                "BatchCode",
//                "BatchSerial",
//                "NIC",
//                "Special Instructions",
//                "Location Type",
//                "Location Comment",
//                "Delivery Medium",
//                "Refundable Amount",
//                "Refunded Amount",
//                "Refunded By",
//                "Refund Ref No",
//                "Delivery Type",
//                "Is Samsung Order",
//                "IP Address",
//                "Country",
//                "Order Verified Date & Time Stamp",
//                "Order Approved Date & Time Stamp",
//                "Comment 1",
//                "Comment 2",
//                "Comment 3"
//        );
//    }

    @Override
    public Map<String, Long> getTotalCounts(List<SelectedFilterDto> filters) {
        return Map.of();
    }

    private Specification<OrderMaster> createSpecification(List<SelectedFilterDto> filters) {
        Specification<OrderMaster> spec = (root, query, cb) -> cb.equal(root.get(EVENT_ORDER_TYPE), ONLINE);

        if (filters == null || filters.isEmpty()) {
            return spec;
        }

        Set<String> filterParams = filters.stream()
                .map(SelectedFilterDto::getFilterParameter)
                .collect(Collectors.toSet());

        if (filterParams.contains("customerName")) {
            List<String> values = getSelectedValues(filters, "customerName");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get("user").get("id").in(
                        values.stream()
                                .map(Long::parseLong)
                                .collect(Collectors.toList())
                ));
            }
        }

        if (filterParams.contains("orderDate")) {
            Optional<DateRange> dateRange = getSelectedDateRange(filters);
            if (dateRange.isPresent()) {
                DateRange range = dateRange.get();
                spec = spec.and((root, query, cb) -> cb.between(
                        root.get("orderDate"),
                        range.startDate().atStartOfDay(),
                        range.endDate().atTime(23, 59, 59)
                ));
            }
        }

        if (filterParams.contains("status")) {
            List<String> values = getSelectedValues(filters, "status");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get("status").in(values));

            }
        }

        // If no filters matched, return a specification that always evaluates to true
        return spec;
    }

    private List<String> getSelectedValues(List<SelectedFilterDto> filters, String filterParameter) {
        return filters.stream()
                .filter(filter -> filter.getFilterParameter().equals(filterParameter))
                .flatMap(filter -> filter.getOptions().stream())
                .map(FilterOptionDto::getCode)
                .toList();
    }

    private Optional<DateRange> getSelectedDateRange(List<SelectedFilterDto> filters) {
        Optional<String> dateRangeValue = filters.stream()
                .filter(filter -> filter.getFilterParameter().equals("orderDate"))
                .map(SelectedFilterDto::getValue)
                .findFirst();

        if (dateRangeValue.isEmpty()) {
            return Optional.empty();
        }

        String[] dates = dateRangeValue.get().split("~");
        if (dates.length != 2) {
            return Optional.empty();
        }

        try {
            LocalDate startDate = LocalDate.parse(dates[0]);
            LocalDate endDate = LocalDate.parse(dates[1]);
            return Optional.of(new DateRange(startDate, endDate));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
