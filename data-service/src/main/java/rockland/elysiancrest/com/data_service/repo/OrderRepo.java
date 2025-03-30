package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface OrderRepo extends JpaRepository<OrderMaster, Long>, JpaSpecificationExecutor<OrderMaster> {

    List<OrderMaster> findByUserId(Long userId);

    List<OrderMaster> findBySessionId(String sessionId);

    record CustomerNameAndUserId(String customerName, Long userId) {}

    @Query("SELECT DISTINCT new rockland.elysiancrest.com.data_service.repo.OrderRepo$CustomerNameAndUserId(o.customerName, o.user.id) FROM OrderMaster o")
    List<CustomerNameAndUserId> findAllCustomerNamesAndUserIds();

    @Query("SELECT DISTINCT o.orderDate FROM OrderMaster o")
    List<LocalDateTime> findAllOrderDates();

    default Page<LocalDate> findDistinctOrderDates(org.springframework.data.jpa.domain.Specification<OrderMaster> spec,
                                                 Pageable pageable) {
        Page<OrderMaster> orders = findAll(spec, pageable);

        List<LocalDate> uniqueDates = orders.getContent().stream()
                .map(order -> order.getOrderDate().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), uniqueDates.size());

        List<LocalDate> pageContent = uniqueDates.subList(start, end);
        return new PageImpl<>(pageContent, pageable, uniqueDates.size());
    }

    default List<OrderMaster> findAllByOrderDate(LocalDateTime startDate,
                                               LocalDateTime endDate,
                                               org.springframework.data.jpa.domain.Specification<OrderMaster> spec) {
        return findAll(
            Specification.where(spec)
                .and((root, query, cb) ->
                    cb.and(
                        cb.greaterThanOrEqualTo(root.get("orderDate"), startDate),
                        cb.lessThan(root.get("orderDate"), endDate)
                    )
                )
        );
    }

    List<OrderMaster> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    record CityNameAndId(String cityName, Long cityId) {}

    @Query("SELECT DISTINCT new rockland.elysiancrest.com.data_service.repo.OrderRepo$CityNameAndId(o.cityName, o.cityId) FROM OrderMaster o")
    List<CityNameAndId> findAllCityNames();

    Optional<OrderMaster> findFirstByOrderByOrderDateAsc();
}
