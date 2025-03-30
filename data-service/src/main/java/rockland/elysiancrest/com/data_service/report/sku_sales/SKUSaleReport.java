package rockland.elysiancrest.com.data_service.report.sku_sales;

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
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.repo.OrderItemMasterRepository;
import rockland.elysiancrest.com.data_service.repo.ProductMasterRepo;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SKUSaleReport implements ReportSpecification<SKUSaleReportDto> {

    private final OrderItemMasterRepository orderRepo;

    private final ProductMasterRepo productMasterRepo;

    @Override
    public String getReportName() {
        return "SKU-Sale-Report";
    }

    @Override
    public Page<SKUSaleReportDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<OrderItemMaster> spec = createSpecification(filters);

        Page<OrderItemMaster> allOrders = orderRepo.findAll(spec, pageable);

        List<SKUSaleReportDto> dtos = allOrders.stream()
                .map(orderItem -> {
                    BigDecimal itemTotal = orderItem.getTotalPriceInRupee();
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    // Create and populate the DTO
                    var dto = new SKUSaleReportDto();
                    dto.setSkuCode(orderItem.getSku());
                    dto.setProductName(orderItem.getProductName());
                    dto.setQuantity(orderItem.getQuantity());
                    dto.setTotalPriceInRupee(decimalFormat.format(itemTotal));

                    return dto;
                })
                .toList();

        // Wrap the DTO list into a pageable structure
        return new PageImpl<>(dtos, pageable, allOrders.getTotalElements());
    }

    @Override
    public List<SKUSaleReportDto> getFilteredData(List<SelectedFilterDto> filters) {
        Specification<OrderItemMaster> spec = createSpecification(filters);

        List<OrderItemMaster> allOrders = orderRepo.findAll(spec);

        return allOrders.stream()
                .map(orderItem -> {
                    BigDecimal itemTotal = orderItem.getTotalPriceInRupee();
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

                    // Create and populate the DTO
                    var dto = new SKUSaleReportDto();
                    dto.setSkuCode(orderItem.getSku());
                    dto.setProductName(orderItem.getProductName());
                    dto.setQuantity(orderItem.getQuantity());
                    dto.setTotalPriceInRupee(decimalFormat.format(itemTotal));

                    return dto;
                })
                .toList();
    }

    @Override
    public List<FilterDto> getFilterOptions() {
        List<FilterDto> filterOptions = new ArrayList<>();

        List<String> skuCodes = productMasterRepo.findAllSku();
        List<FilterOptionDto> skuOptions = skuCodes.stream()
                .map(skuCode -> new FilterOptionDto(skuCode, skuCode))
                .toList();
        filterOptions.add(new FilterDto("SKU Code", "skuCode", "multi-select", skuOptions, "Select SKU code"));

        List<String> productNames = productMasterRepo.findAllCorrectNames();
        List<FilterOptionDto> productOptions = productNames.stream()
                .map(name -> new FilterOptionDto(name, name))
                .toList();
        filterOptions.add(new FilterDto("Product Name", "productName", "multi-select", productOptions, "Select SKU code"));

        return filterOptions;
    }

    @Override
    public Class<SKUSaleReportDto> getType() {
        return SKUSaleReportDto.class;
    }

//    @Override
//    public List<String> getHeaders() {
//        return List.of(
//                "SKU Code",
//                "Product Name",
//                "Number of Items",
//                "Total Sale (LKR)"
//        );
//    }

    @Override
    public Map<String, Long> getTotalCounts(List<SelectedFilterDto> filters) {
        return Map.of();
    }

    private Specification<OrderItemMaster> createSpecification(List<SelectedFilterDto> filters) {
        if (filters == null || filters.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        Specification<OrderItemMaster> spec = Specification.where(null);

        Set<String> filterParams = filters.stream()
                .map(SelectedFilterDto::getFilterParameter)
                .collect(Collectors.toSet());

        if (filterParams.contains("skuCode")) {
            List<String> values = getSelectedValues(filters, "skuCode");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get("sku").in(values));

            }
        }

        if (filterParams.contains("productName")) {
            List<String> values = getSelectedValues(filters, "productName");
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get("product_name").in(values));

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
}
