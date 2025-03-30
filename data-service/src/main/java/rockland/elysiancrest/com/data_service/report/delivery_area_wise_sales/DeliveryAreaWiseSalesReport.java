package rockland.elysiancrest.com.data_service.report.delivery_area_wise_sales;

import com.commonlibrary.contract.v1.report.FilterDto;
import com.commonlibrary.contract.v1.report.FilterOptionDto;
import com.commonlibrary.contract.v1.report.SelectedFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.report.ReportSpecification;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeliveryAreaWiseSalesReport implements ReportSpecification<DeliveryAreaWiseSalesReportDto> {

    private static final String UNKNOWN = "Unknown";
    private static final String ONLINE = "online";
    private static final String CITY_NAME = "cityName";
    private static final String CITY_ID = "cityId";
    private static final String EVENT_ORDER_TYPE = "eventOrderType";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00");

    private record OrderDetails(String cityName, BigDecimal priceInRupee, BigDecimal deliveryFeeInRupee) {}

    private final OrderRepo orderRepo;

    @Override
    public String getReportName() {
        return "Delivery-Area-Wise-Sales-Report";
    }

    @Override
    public Page<DeliveryAreaWiseSalesReportDto> getFilteredData(List<SelectedFilterDto> filters, int page, int size) {
        List<DeliveryAreaWiseSalesReportDto> dtos = processOrders(filters);
        return createPage(dtos, page, size);
    }

    @Override
    public List<DeliveryAreaWiseSalesReportDto> getFilteredData(List<SelectedFilterDto> filters) {
        return processOrders(filters);
    }

    private List<DeliveryAreaWiseSalesReportDto> processOrders(List<SelectedFilterDto> filters) {
        List<OrderMaster> allOrders = orderRepo.findAll(createSpecification(filters));
        ConcurrentHashMap<String, List<OrderDetails>> cityWiseOrderDetails = groupOrdersByCity(allOrders);
        return createReportDtos(cityWiseOrderDetails);
    }

    private ConcurrentHashMap<String, List<OrderDetails>> groupOrdersByCity(List<OrderMaster> orders) {
        ConcurrentHashMap<String, List<OrderDetails>> cityWiseOrderDetails = new ConcurrentHashMap<>();
        
        orders.parallelStream()
                .map(this::createOrderDetails)
                .forEach(orderDetails -> 
                    cityWiseOrderDetails.computeIfAbsent(orderDetails.cityName(), k -> new ArrayList<>())
                                      .add(orderDetails));
                                      
        return cityWiseOrderDetails;
    }

    private OrderDetails createOrderDetails(OrderMaster order) {
        return new OrderDetails(
            order.getCityName() != null ? order.getCityName() : UNKNOWN,
            order.getTotalPriceInRupee() != null ? order.getTotalPriceInRupee() : BigDecimal.ZERO,
            order.getDeliveryFeeInRupee() != null ? order.getDeliveryFeeInRupee() : BigDecimal.ZERO
        );
    }

    private List<DeliveryAreaWiseSalesReportDto> createReportDtos(ConcurrentHashMap<String, List<OrderDetails>> cityWiseOrderDetails) {
        return cityWiseOrderDetails.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(this::createReportDto)
                .collect(Collectors.toList());
    }

    private DeliveryAreaWiseSalesReportDto createReportDto(Map.Entry<String, List<OrderDetails>> entry) {
        String cityName = entry.getKey();
        List<OrderDetails> orders = entry.getValue();
        
        DeliveryAreaWiseSalesReportDto dto = new DeliveryAreaWiseSalesReportDto();
        dto.setDeliveryCity(cityName);
        dto.setOrderCount(orders.size());
        
        BigDecimal totalPrice = calculateTotal(orders, OrderDetails::priceInRupee);
        dto.setGrossSale(DECIMAL_FORMAT.format(totalPrice));
        
        BigDecimal totalDeliveryFee = calculateTotal(orders, OrderDetails::deliveryFeeInRupee);
        dto.setDeliveryFee(DECIMAL_FORMAT.format(totalDeliveryFee));
        
        dto.setNetSale(DECIMAL_FORMAT.format(totalPrice.subtract(totalDeliveryFee)));
        
        return dto;
    }

    private BigDecimal calculateTotal(List<OrderDetails> orders, java.util.function.Function<OrderDetails, BigDecimal> mapper) {
        return orders.stream()
                .map(mapper)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Page<DeliveryAreaWiseSalesReportDto> createPage(List<DeliveryAreaWiseSalesReportDto> dtos, int page, int size) {
        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min((start + size), dtos.size());
        List<DeliveryAreaWiseSalesReportDto> pageContent = dtos.subList(start, end);
        return new PageImpl<>(pageContent, PageRequest.of(page, size), dtos.size());
    }

    @Override
    public List<FilterDto> getFilterOptions() {
        List<FilterDto> filterOptions = new ArrayList<>();
        List<OrderRepo.CityNameAndId> cities = orderRepo.findAllCityNames();
        List<FilterOptionDto> cityOptions = cities.stream()
                .map(city -> new FilterOptionDto(String.valueOf(city.cityId()), city.cityName()))
                .toList();
        filterOptions.add(new FilterDto("City Name", CITY_NAME, "multi-select", cityOptions, "Select city"));
        return filterOptions;
    }

    @Override
    public Class<DeliveryAreaWiseSalesReportDto> getType() {
        return DeliveryAreaWiseSalesReportDto.class;
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

        if (filterParams.contains(CITY_NAME)) {
            List<String> values = getSelectedValues(filters);
            if (!values.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get(CITY_ID).in(values));
            }
        }

        return spec;
    }

    private List<String> getSelectedValues(List<SelectedFilterDto> filters) {
        return filters.stream()
                .filter(filter -> filter.getFilterParameter().equals(CITY_NAME))
                .flatMap(filter -> filter.getOptions().stream())
                .map(FilterOptionDto::getCode)
                .toList();
    }
}
