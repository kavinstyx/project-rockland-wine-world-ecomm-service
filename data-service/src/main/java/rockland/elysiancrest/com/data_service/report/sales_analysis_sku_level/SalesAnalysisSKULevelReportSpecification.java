package rockland.elysiancrest.com.data_service.report.sales_analysis_sku_level;

import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.FilterOptionDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.repo.ProductMasterRepo;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SalesAnalysisSKULevelReportSpecification implements ReportSpecification<SalesAnalysisSKUDto> {

    private static final String ONLINE = "online";
    private static final String EVENT_ORDER_TYPE = "eventOrderType";

    private final ProductMasterRepo productMasterRepo;
    private final OrderRepo orderRepo;
    private final ModelMapper modelMapper;

    @Override
    public String getReportName() {
        return "Sales-Analysis-SKU-Level-Report";
    }

    @Override
    public Page<SalesAnalysisSKUDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<OrderMaster> spec = createSpecification(filters);

        Page<OrderMaster> allOrders = orderRepo.findAll(spec, pageable);

        List<SalesAnalysisSKUDto> dtos = allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream()
                        .map(orderItem -> createDto(order, orderItem)))
                .toList();

        return new PageImpl<>(dtos, pageable, allOrders.getTotalElements());
    }

    @Override
    public List<SalesAnalysisSKUDto> getFilteredData(List<SelectedFilterDto> filters) {
        Specification<OrderMaster> spec = createSpecification(filters);
        List<OrderMaster> allOrders = orderRepo.findAll(spec);

        return allOrders.stream()
                .flatMap(order -> order.getOrderItems().stream()
                        .map(orderItem -> createDto(order, orderItem)))
                .toList();
    }

    @Override
    public List<FilterDto> getFilterOptions() {
        List<FilterDto> filterOptions = new ArrayList<>();

        // Branches filter
        List<String> allSkus = productMasterRepo.findAllSku();
        List<FilterOptionDto> skuOptions = allSkus.stream()
                .map(sku -> new FilterOptionDto(sku, sku))
                .distinct()
                .collect(Collectors.toList());
        filterOptions.add(new FilterDto("SKU", "sku", "multi-select", skuOptions, "Select SKU"));

        List<LocalDateTime> allOrderDates = orderRepo.findAllOrderDates();
        List<FilterOptionDto> orderDatesOptions = allOrderDates.stream()
                .map(orderDate -> new FilterOptionDto(orderDate.toString(), orderDate.toString()))
                .distinct()
                .toList();
        filterOptions.add(new FilterDto("Order Date", "orderDate", "date-range", orderDatesOptions, "Select order date"));

        List<ProductMaster> allProductNames = productMasterRepo.findAll();
        List<FilterOptionDto> productOptions = allProductNames.stream()
                .map(product -> new FilterOptionDto(product.getNavItemCode(), product.getCorrectName()))
                .distinct()
                .toList();
        filterOptions.add(new FilterDto("Product Name", "productName", "multi-select", productOptions, "Select product"));

        return filterOptions;
    }

    @Override
    public Class<SalesAnalysisSKUDto> getType() {
        return SalesAnalysisSKUDto.class;
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

        if (filterParams.contains("sku")) {
            List<String> values = getSelectedValues(filters, "sku");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> {
                    Join<?, ?> orderItems = root.join("orderItems");
                    return orderItems.get("product").get("sku").in(values);
                });
            }
        }

        if (filterParams.contains("orderDate")) {
            List<String> values = getSelectedValues(filters, "orderDate");
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

        if (filterParams.contains("productName")) {
            List<String> values = getSelectedValues(filters, "productName");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> {
                    Join<?, ?> orderItems = root.join("orderItems");
                    return cb.like(cb.lower(orderItems.get("productName")), "%" + values.get(0).toLowerCase() + "%");
                });
            }
        }

        if (filterParams.contains("quantity")) {
            List<String> values = getSelectedValues(filters, "quantity");
            if (!values.isEmpty()) {
                try {
                    Integer quantity = Integer.parseInt(values.get(0));
                    spec = spec.and((root, query, cb) -> {
                        Join<?, ?> orderItems = root.join("orderItems");
                        return cb.equal(orderItems.get("quantity"), quantity);
                    });
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid quantity format: " + values.get(0));
                }
            }
        }

        // If no filters matched, return a specification that always evaluates to true
        return spec == null ? (root, query, cb) -> cb.conjunction() : spec;
    }

    private List<String> getSelectedValues(List<SelectedFilterDto> filters, String filterParameter) {
        return filters.stream()
                .filter(filter -> filter.getFilterParameter().equals(filterParameter))
                .flatMap(filter -> filter.getOptions().stream())
                .map(FilterOptionDto::getCode)
                .collect(Collectors.toList());
    }

    private SalesAnalysisSKUDto createDto(OrderMaster order, OrderItemMaster orderItem) {
        LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(order.getOrderDate()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = dateTime.format(formatter);

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

        // Create and populate the DTO
        var dto = new SalesAnalysisSKUDto();
        dto.setSku(orderItem.getProduct().getSku());
        dto.setProductName(orderItem.getProductName());
        
        // Safe formatting for price
        BigDecimal unitPrice = orderItem.getUnitPriceInRupee();
        dto.setPrice(unitPrice != null ? decimalFormat.format(unitPrice) : "0.00");
        
        dto.setQuantity(orderItem.getQuantity());
        
        // Safe formatting for total
        BigDecimal totalPrice = orderItem.getTotalPriceInRupee();
        dto.setTotal(totalPrice != null ? decimalFormat.format(totalPrice) : "0.00");
        
        dto.setDate(formattedDate);
        dto.setOrderNumber(order.getId());
        return dto;
    }

}
