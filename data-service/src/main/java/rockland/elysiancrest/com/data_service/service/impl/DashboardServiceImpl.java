package rockland.elysiancrest.com.data_service.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.dto.dashboard.OrderCategoryDataDTO;
import rockland.elysiancrest.com.data_service.dto.dashboard.OrderDataDTO;
import rockland.elysiancrest.com.data_service.dto.dashboard.OrderProductDataDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.CategoryMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.repo.SalesTargetRepo;
import rockland.elysiancrest.com.data_service.service.DashboardService;
import rockland.elysiancrest.com.data_service.entity.sales.SalesTarget;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final OrderRepo orderRepo;
    private final SalesTargetRepo salesTargetRepo;

    public DashboardServiceImpl(OrderRepo orderRepo, SalesTargetRepo salesTargetRepo) {
        this.orderRepo = orderRepo;
        this.salesTargetRepo = salesTargetRepo;
    }

    @Override
    public Response<OrderDataDTO> getOrderData(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderMaster> orders;
        if (startDate != null && endDate != null) {
            orders = orderRepo.findByOrderDateBetween(startDate, endDate);
        } else {
            orders = orderRepo.findAll();
        }

        OrderDataDTO orderData = new OrderDataDTO();

        // Calculate total sales and delivery cost
        BigDecimal totalSales = orders.stream()
                .map(order -> order.getTotalPriceInRupee() != null ? order.getTotalPriceInRupee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryCost = orders.stream()
                .map(order -> order.getDeliveryFeeInRupee() != null ? order.getDeliveryFeeInRupee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderData.setTotalSale(totalSales.doubleValue());
        orderData.setDeliveryCost(deliveryCost.doubleValue());
        orderData.setNetSales(totalSales.subtract(deliveryCost).doubleValue());
        orderData.setNumberOfOrders(orders.size());

        // Calculate average order value
        if (!orders.isEmpty()) {
            orderData.setAverageOrderValue(totalSales.doubleValue() / orders.size());

            // Calculate average SKUs per order
            double totalSkus = orders.stream()
                    .mapToInt(order -> order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                    .sum();
            orderData.setAverageSkusPerOrder(totalSkus / orders.size());
        }

        // Calculate top 10 products sold
        List<OrderProductDataDTO> topProducts = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(item -> item.getProduct() != null)
                .collect(groupingBy(
                        OrderItemMaster::getProduct,
                        summingInt(OrderItemMaster::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<ProductMaster, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    ProductMaster product = entry.getKey();
                    OrderProductDataDTO dto = new OrderProductDataDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getCorrectName());
                    dto.setSku(product.getSku());
                    return dto;
                })
                .toList();

        orderData.setTopProductsSold(topProducts);

        // Calculate top 10 categories sold
        List<OrderCategoryDataDTO> topCategories = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(item -> item.getProduct() != null && item.getProduct().getCategory() != null)
                .collect(groupingBy(
                        item -> item.getProduct().getCategory(),
                        summingInt(OrderItemMaster::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<CategoryMaster, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    CategoryMaster category = entry.getKey();
                    OrderCategoryDataDTO dto = new OrderCategoryDataDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getCategoryName());
                    return dto;
                })
                .toList();

        orderData.setTopCategoriesSold(topCategories);

        // Get sales targets for the same period
        if (startDate == null || endDate == null) {
            // Get the first order date from the system
            LocalDateTime firstOrderDate = orderRepo.findFirstByOrderByOrderDateAsc()
                    .map(OrderMaster::getOrderDate)
                    .orElse(LocalDateTime.now());
            
            startDate = startDate != null ? startDate : firstOrderDate;
            endDate = endDate != null ? endDate : LocalDateTime.now();
        }

        List<SalesTarget> salesTargets = salesTargetRepo.findByYearAndMonthBetween(
                startDate.getYear(),
                startDate.getMonthValue(),
                endDate.getYear(),
                endDate.getMonthValue()
        );

        // Set target values
        orderData.setTotalSaleTarget(salesTargets.stream().mapToDouble(SalesTarget::getTotalSale).sum());
        orderData.setDeliveryCostTarget(salesTargets.stream().mapToDouble(SalesTarget::getDeliveryCost).sum());
        orderData.setNetSalesTarget(salesTargets.stream().mapToDouble(SalesTarget::getNetSales).sum());
        orderData.setNumberOfOrdersTarget(salesTargets.stream().mapToInt(SalesTarget::getNumberOfOrders).sum());
        
        // Calculate target averages
        orderData.setAverageOrderValueTarget(salesTargets.stream()
                .mapToDouble(SalesTarget::getAverageOrderValue)
                .average()
                .orElse(0.0));
        orderData.setAverageSkusPerOrderTarget(salesTargets.stream()
                .mapToDouble(SalesTarget::getAverageSkusPerOrder)
                .average()
                .orElse(0.0));

        return Response.<OrderDataDTO>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(orderData)
                .message("Order data retrieved successfully")
                .build();
    }


}
