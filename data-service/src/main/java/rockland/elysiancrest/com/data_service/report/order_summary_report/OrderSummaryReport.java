package rockland.elysiancrest.com.data_service.report.order_summary_report;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.FilterOptionDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
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
public class OrderSummaryReport implements ReportSpecification<OrderSummaryReportDto> {

    private static final String ONLINE = "online";
    private static final String EVENT_ORDER_TYPE = "eventOrderType";

    private final OrderRepo orderRepo;

    @Override
    public String getReportName() {
        return "Order-Summary-Report";
    }

    @Override
    public Page<OrderSummaryReportDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<OrderMaster> spec = createSpecification(filters);

        Page<OrderMaster> allOrders = orderRepo.findAll(spec, pageable);

        List<OrderSummaryReportDto> dtos = allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream().map(orderItem -> {
                    BigDecimal itemSubTotal = BigDecimal.ZERO;
                    if (orderItem.getUnitPriceInRupee() != null) {
                        itemSubTotal = orderItem.getUnitPriceInRupee().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    }
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    LocalDateTime dateTime = order.getOrderDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = dateTime.format(formatter);

                    var dto = new OrderSummaryReportDto();
                    dto.setOrderDate(formattedDate);
                    dto.setOrderNumber(order.getId());
                    dto.setCustomerName(order.getCustomerName());
                    dto.setItemSubTotal(decimalFormat.format(itemSubTotal));
                    dto.setDeliveryFee(decimalFormat.format(order.getDeliveryFeeInRupee() != null ? order.getDeliveryFeeInRupee() : BigDecimal.ZERO));
                    dto.setTotal(decimalFormat.format(orderItem.getTotalPriceInRupee() != null ? orderItem.getTotalPriceInRupee() : BigDecimal.ZERO));
                    dto.setDeliveryDate(order.getDeliveryDate());
                    dto.setStatus(String.valueOf(order.getStatus()));
                    return dto;
                }))
                .collect(Collectors.groupingBy(
                        OrderSummaryReportDto::getOrderNumber,
                        Collectors.reducing(
                                new OrderSummaryReportDto(),
                                dto -> dto,
                                (dto1, dto2) -> {
                                    // Combine DTOs for the same order
                                    if (dto1.getOrderDate() != null && dto2.getOrderDate() == null) {
                                        dto2.setOrderDate(dto1.getOrderDate());
                                    }
                                    if (dto1.getOrderNumber() != null && dto2.getOrderNumber() == null) {
                                        dto2.setOrderNumber(dto1.getOrderNumber());
                                    }
                                    if (dto1.getCustomerName() != null && dto2.getCustomerName() == null) {
                                        dto2.setCustomerName(dto1.getCustomerName());
                                    }

                                    dto2.setItemSubTotal(addFormattedBigDecimals(dto1.getItemSubTotal(), dto2.getItemSubTotal()));
                                    dto2.setDeliveryFee(addFormattedBigDecimals(dto1.getDeliveryFee(), dto2.getDeliveryFee()));
                                    dto2.setTotal(addFormattedBigDecimals(dto1.getTotal(), dto2.getTotal()));

                                    if (dto1.getDeliveryDate() != null && dto2.getDeliveryDate() == null) {
                                        dto2.setDeliveryDate(dto1.getDeliveryDate());
                                    }
                                    if (dto1.getStatus() != null && dto2.getStatus() == null) {
                                        dto2.setStatus(dto1.getStatus());
                                    }

                                    return dto2;
                                }
                        )
                ))
                .values()
                .stream()
                .toList();

        // Wrap the DTO list into a pageable structure
        return new PageImpl<>(dtos, pageable, allOrders.getTotalElements());
    }

    @Override
    public List<OrderSummaryReportDto> getFilteredData(List<SelectedFilterDto> filters) {
        Specification<OrderMaster> spec = createSpecification(filters);

        List<OrderMaster> allOrders = orderRepo.findAll(spec);

        return allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream().map(orderItem -> {
                    BigDecimal itemSubTotal = BigDecimal.ZERO;
                    if (orderItem.getUnitPriceInRupee() != null) {
                        itemSubTotal = orderItem.getUnitPriceInRupee().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    }
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    LocalDateTime dateTime = order.getOrderDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = dateTime.format(formatter);

                    var dto = new OrderSummaryReportDto();
                    dto.setOrderDate(formattedDate);
                    dto.setOrderNumber(order.getId());
                    dto.setCustomerName(order.getCustomerName());
                    dto.setItemSubTotal(decimalFormat.format(itemSubTotal));
                    dto.setDeliveryFee(decimalFormat.format(order.getDeliveryFeeInRupee() != null ? order.getDeliveryFeeInRupee() : BigDecimal.ZERO));
                    dto.setTotal(decimalFormat.format(orderItem.getTotalPriceInRupee() != null ? orderItem.getTotalPriceInRupee() : BigDecimal.ZERO));
                    dto.setDeliveryDate(order.getDeliveryDate());
                    dto.setStatus(String.valueOf(order.getStatus()));
                    return dto;
                }))
                .collect(Collectors.groupingBy(
                        OrderSummaryReportDto::getOrderNumber,
                        Collectors.reducing(
                                new OrderSummaryReportDto(),
                                dto -> dto,
                                (dto1, dto2) -> {
                                    // Combine DTOs for the same order
                                    if (dto1.getOrderDate() != null && dto2.getOrderDate() == null) {
                                        dto2.setOrderDate(dto1.getOrderDate());
                                    }
                                    if (dto1.getOrderNumber() != null && dto2.getOrderNumber() == null) {
                                        dto2.setOrderNumber(dto1.getOrderNumber());
                                    }
                                    if (dto1.getCustomerName() != null && dto2.getCustomerName() == null) {
                                        dto2.setCustomerName(dto1.getCustomerName());
                                    }

                                    dto2.setItemSubTotal(addFormattedBigDecimals(dto1.getItemSubTotal(), dto2.getItemSubTotal()));
                                    dto2.setDeliveryFee(addFormattedBigDecimals(dto1.getDeliveryFee(), dto2.getDeliveryFee()));
                                    dto2.setTotal(addFormattedBigDecimals(dto1.getTotal(), dto2.getTotal()));

                                    if (dto1.getDeliveryDate() != null && dto2.getDeliveryDate() == null) {
                                        dto2.setDeliveryDate(dto1.getDeliveryDate());
                                    }
                                    if (dto1.getStatus() != null && dto2.getStatus() == null) {
                                        dto2.setStatus(dto1.getStatus());
                                    }

                                    return dto2;
                                }
                        )
                ))
                .values()
                .stream()
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
    public Class<OrderSummaryReportDto> getType() {
        return OrderSummaryReportDto.class;
    }


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



    private static String addFormattedBigDecimals(String formattedValue1, String formattedValue2) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        BigDecimal value1 = new BigDecimal(formattedValue1 != null ? formattedValue1.replace(",", "") : "0.00");
        BigDecimal value2 = new BigDecimal(formattedValue2 != null ? formattedValue2.replace(",", "") : "0.00");
        return decimalFormat.format(value1.add(value2));
    }
}
