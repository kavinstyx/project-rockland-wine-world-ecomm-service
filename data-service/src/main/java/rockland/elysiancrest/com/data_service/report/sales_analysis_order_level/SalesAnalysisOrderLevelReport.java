package rockland.elysiancrest.com.data_service.report.sales_analysis_order_level;

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
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalesAnalysisOrderLevelReport implements ReportSpecification<SalesAnalysisOrderLevelDto> {

    private static final String ONLINE = "online";
    private static final String EVENT_ORDER_TYPE = "eventOrderType";

    private final OrderRepo orderRepo;

    @Override
    public String getReportName() {
        return "Sales-Analysis-Order-Level-Report";
    }

    @Override
    public Page<SalesAnalysisOrderLevelDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        Specification<OrderMaster> spec = createSpecification(filters);
        Pageable pageable = PageRequest.of(page, size);
        
        List<OrderMaster> allOrders = orderRepo.findAll(spec);
        
        // Group orders by date
        Map<LocalDate, List<OrderMaster>> ordersByDate = allOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        List<SalesAnalysisOrderLevelDto> dtos = ordersByDate.entrySet().stream()
                .map(entry -> createDailyReport(entry.getKey().atStartOfDay(), entry.getValue()))
                .sorted(Comparator.comparing(dto -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return LocalDate.parse(dto.getDate(), formatter);
                }))
                .collect(Collectors.toList());

        // Apply pagination to the sorted DTOs
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<SalesAnalysisOrderLevelDto> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Override
    public List<SalesAnalysisOrderLevelDto> getFilteredData(List<SelectedFilterDto> filters) {
        Specification<OrderMaster> spec = createSpecification(filters);
        List<OrderMaster> allOrders = orderRepo.findAll(spec);
        
        // Group orders by date
        Map<LocalDate, List<OrderMaster>> ordersByDate = allOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));
        return ordersByDate.entrySet().stream()
                .map(entry -> createDailyReport(entry.getKey().atStartOfDay(), entry.getValue()))
                .sorted(Comparator.comparing(dto -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return LocalDate.parse(dto.getDate(), formatter);
                }))
                .collect(Collectors.toList());
    }

    @Override
    public List<FilterDto> getFilterOptions() {
        List<FilterDto> filterOptions = new ArrayList<>();

        List<LocalDateTime> allOrderDates = orderRepo.findAllOrderDates();
        List<FilterOptionDto> orderDatesOptions = allOrderDates.stream()
                .map(orderDate -> new FilterOptionDto(orderDate.toString(), orderDate.toString()))
                .distinct()
                .toList();
        filterOptions.add(new FilterDto("Order Date", "orderDate", "multi-select", orderDatesOptions, "Select order date"));

        return filterOptions;
    }

//    @Override
//    public List<String> getHeaders() {
//        return List.of(
//                "Date",
//                "Day",
//                "Order Count",
//                "Gross Sale (LKR)",
//                "Delivery Fee (LKR)",
//                "Net Sale (LKR)",
//                "Average Basket Value (Gross - LKR)",
//                "Average Basket Value (Net - LKR)"
//        );
//    }

    @Override
    public Class<SalesAnalysisOrderLevelDto> getType() {
        return SalesAnalysisOrderLevelDto.class;
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

        if (filterParams.contains("date")) {
            List<String> values = getSelectedValues(filters, "date");
            if (!values.isEmpty()) {
                List<LocalDate> dates = values.stream()
                        .map(dateStr -> {
                            try {
                                return LocalDate.parse(dateStr);
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Invalid date format: " + dateStr);
                            }
                        })
                        .collect(Collectors.toList());
                spec = spec.and((root, query, cb) -> root.get("orderDate").in(dates));
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
                .collect(Collectors.toList());
    }

    private SalesAnalysisOrderLevelDto createDailyReport(LocalDateTime date, List<OrderMaster> dailyOrders) {
        Integer orderCount = 0;
        BigDecimal grossSales = BigDecimal.ZERO;
        BigDecimal netSales = BigDecimal.ZERO;
        BigDecimal averageBasketValueGross = BigDecimal.ZERO;
        BigDecimal averageBasketValueNet = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        // Calculate daily metrics
        orderCount = dailyOrders.size(); // Number of orders for the day
        
        // Calculate total gross sales (total order value)
        grossSales = dailyOrders.stream()
                .map(OrderMaster::getTotalPriceInRupee)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total delivery fees
        deliveryFee = dailyOrders.stream()
                .map(OrderMaster::getDeliveryFeeInRupee)
                .filter(fee -> fee != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net sales (gross sales - delivery fees)
        netSales = grossSales.subtract(deliveryFee);

        // Calculate average basket values
        averageBasketValueGross = orderCount > 0 ? 
            grossSales.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

        return SalesAnalysisOrderLevelDto.builder()
                .date(formattedDate)
                .day(date.getDayOfWeek().name())
                .orderCount(orderCount)
                .grossSale(decimalFormat.format(grossSales))
                .deliveryFee(decimalFormat.format(deliveryFee))
                .netSale(decimalFormat.format(netSales))
                .averageBasketValueGross(decimalFormat.format(averageBasketValueGross))
                .averageBasketValueNet(decimalFormat.format(averageBasketValueGross)) // Same as gross since we're not calculating net differently
                .build();
    }
}
