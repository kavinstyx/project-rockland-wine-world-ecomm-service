//package rockland.elysiancrest.com.data_service.service;
//
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Root;
//import org.springframework.data.jpa.domain.Specification;
//import rockland.elysiancrest.com.data_service.dto.ProductFilterDTO;
//import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductSpecification {
//
//    public static Specification<ProductMaster> filterBySizeCategory(String sizeCategory) {
//        return (root, query, cb) -> {
//            switch (sizeCategory) {
//                case "small":
//                    return cb.lessThan(root.get("bottleSizeMl"), 50);
//                case "medium":
//                    return cb.between(root.get("bottleSizeMl"), 50, 500);
//                case "large":
//                    return cb.between(root.get("bottleSizeMl"), 500, 750);
//                case "extraLarge":
//                    return cb.greaterThan(root.get("bottleSizeMl"), 750);
//                default:
//                    // Throwing an exception to handle unexpected size category
//                    throw new IllegalArgumentException("Invalid size category: " + sizeCategory);
//            }
//        };
//    }
//
//    public static Specification<ProductMaster> filterByBrands(List<String> brands) {
//        return (root, query, cb) -> {
//            return root.join("brand").get("brandName").in(brands);
//        };
//    }
//
//    public static Specification<ProductMaster> isFastMoving() {
//        // Adjusted to use the `isBestSeller` field
//        return (root, query, cb) -> cb.isTrue(root.get("isBestSeller"));
//    }
//
//    public static Specification<ProductMaster> createSpecification(ProductFilterDTO filter) {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (filter.getSizeCategory() != null) {
//                predicates.add(filterBySizeCategory(filter.getSizeCategory()).toPredicate(root, query, cb));
//            }
//            if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
//                predicates.add(filterByBrands(filter.getBrands()).toPredicate(root, query, cb));
//            }
//
//            if (!predicates.isEmpty()) {
//                return cb.and(predicates.toArray(new Predicate[0]));
//            } else {
//                return cb.conjunction();
//            }
//        }; List<Specification<ProductMaster>> specifications = new ArrayList<>();
//
//        if (filter.getSizeCategory() != null) {
//            specifications.add(filterBySizeCategory(filter.getSizeCategory()));
//        }
//        if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
//            specifications.add(filterByBrands(filter.getBrands()));
//        }
//        if (filter.getSortBy() != null) {
//            applySorting(filter.getSortBy(), root, query, cb);
//        }
//
//        Specification<ProductMaster> result = specifications.get(0);
//        for (int i = 1; i < specifications.size(); i++) {
//            result = result.and(specifications.get(i));
//        }
//        return result;
//    }
//
//    private static void applySorting(String sort, Root<ProductMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//        switch (sort) {
//            case "price_low_high":
//                query.orderBy(cb.asc(root.get("regularPrice")));
//                break;
//            case "price_high_low":
//                query.orderBy(cb.desc(root.get("regularPrice")));
//                break;
//            case "fast_moving":
//                query.orderBy(cb.desc(root.get("isBestSeller"))); // Assumption applied here
//                break;
//        }
//    }
//
//}
