//package rockland.elysiancrest.com.data_service.service.impl;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import jakarta.persistence.criteria.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Component;
//import rockland.elysiancrest.com.data_service.dto.ProductFilterDTO;
//import rockland.elysiancrest.com.data_service.entity.master_data.BrandMaster;
//import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
//import rockland.elysiancrest.com.data_service.service.ProductFilterService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class ProductFilterServiceImpl implements ProductFilterService {
//
//    private EntityManager entityManager;
//
//    public ProductFilterServiceImpl(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    @Override
//    public Page<ProductMaster> findProductsByCriteria(ProductFilterDTO criteria, Pageable pageable) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<ProductMaster> query = cb.createQuery(ProductMaster.class);
//        Root<ProductMaster> product = query.from(ProductMaster.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        //filter by bottle size
//        if(criteria.getSize() != null){
//            switch (criteria.getSize()){
//                case "small":
//                    predicates.add(cb.lessThan(product.get("bottleSizeMl"), 50));
//                    break;
//                case "medium":
//                    predicates.add(cb.between(product.get("bottleSizeMl"), 50, 500));
//                    break;
//                case "large":
//                    predicates.add(cb.between(product.get("bottleSizeMl"), 500, 750));
//                    break;
//                case "extra_large":
//                    predicates.add(cb.greaterThan(product.get("bottleSizeMl"), 750));
//                    break;
//            }
//        }
//
//        //filter by brand
//        if(criteria.getBrand() != null){
//            Join<ProductMaster, BrandMaster> brandJoin = product.join("brand", JoinType.INNER);
//            predicates.add(cb.equal(brandJoin.get("brand_name"), criteria.getBrand()));
//        }
//
//        //apply filtering
//        query.select(product).where(cb.and(predicates.toArray(new Predicate[0])));
//        // Apply sorting
//        if (criteria.getSort() != null) {
//            switch (criteria.getSort()) {
//                case "price_low_high":
//                    query.orderBy(cb.asc(product.get("regularPrice")));
//                    break;
//                case "price_high_low":
//                    query.orderBy(cb.desc(product.get("regularPrice")));
//                    break;
//                case "fast_moving":
//                    query.orderBy(cb.desc(product.get("isBestSeller"))); // Assuming `isBestSeller` indicates fast-moving products
//                    break;
//            }
//        }
//
//        // Pagination logic
//        TypedQuery<ProductMaster> typedQuery = entityManager.createQuery(query);
//        typedQuery.setFirstResult((int) pageable.getOffset());
//        typedQuery.setMaxResults(pageable.getPageSize());
//        List<ProductMaster> resultList = typedQuery.getResultList();
//
//        // Execute query with pagination
////        List<ProductMaster> products = entityManager.createQuery(query)
////                .setFirstResult((int) pageable.getOffset())
////                .setMaxResults(pageable.getPageSize())
////                .getResultList();
//
//        // Count query for pagination metadata
//        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//        Root<ProductMaster> countRoot = countQuery.from(ProductMaster.class);
//        countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
//        Long count = entityManager.createQuery(countQuery).getSingleResult();
//
//        return new PageImpl<>(resultList, pageable, count);
//    }
//}
