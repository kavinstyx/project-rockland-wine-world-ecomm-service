package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Product;
import com.commonlibrary.contract.v1.ProductFilterValues;
import com.commonlibrary.contract.v1.sap.SAPMaterialItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.cache.InMemoryCache;
import rockland.elysiancrest.com.data_service.dto.DepartmentCategoryProductDTO;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.entity.master_data.*;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.repo.*;
import rockland.elysiancrest.com.data_service.service.ProductMasterService;

import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductMasterServiceImpl extends CrudServiceImpl<ProductMaster, Long, ProductMasterRepo, Product> implements ProductMasterService {

    private final EntityManager entityManager;
    private final BrandMasterRepo brandMasterRepo;
    private final ProductMasterRepo productMasterRepo;
    private final InMemoryCache<String, List<DepartmentCategoryProductDTO>> hierarchyCache;
    private final SellingPriceMasterRepo sellingPriceMasterRepo;
    private final ExchangeRateMasterRepo exchangeRateMasterRepo;
    private final DeliverChargesRepo deliverChargesRepo;
    private final AddonRepo addonRepo;


    public ProductMasterServiceImpl(ProductMasterRepo repository, ModelMapper modelMapper, EntityManager entityManager, BrandMasterRepo brandMasterRepo, ProductMasterRepo productMasterRepo, SellingPriceMasterRepo sellingPriceMasterRepo, ExchangeRateMasterRepo exchangeRateMasterRepo, DeliverChargesRepo deliverChargesRepo, AddonRepo addonRepo) {
        super(repository, modelMapper);
        this.entityManager = entityManager;
        this.brandMasterRepo = brandMasterRepo;
        this.productMasterRepo = productMasterRepo;
        this.sellingPriceMasterRepo = sellingPriceMasterRepo;
        this.exchangeRateMasterRepo = exchangeRateMasterRepo;
        this.deliverChargesRepo = deliverChargesRepo;
        this.addonRepo = addonRepo;
        // Initialize the cache with a 5-minute TTL
        this.hierarchyCache = new InMemoryCache<>(5 * 60 * 1000);
    }

    @Override
    protected Product convertToDto(ProductMaster entity) {
        Product dto = super.convertToDto(entity);

        //set brand id manually
        if (entity.getBrand() != null) {
            dto.setBrandId(entity.getBrand().getId());

        }

        //set category id manually
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getCategoryName());
        }

        //set department id manually
        if (entity.getDepartment() != null) {
            dto.setDepartmentId(entity.getDepartment().getId());
            dto.setDepartmentName(entity.getDepartment().getDepartmentName());
        }
        return dto;
    }

    @Transactional
    @Override
    public Page<Product> searchProducts(Long brandId, Long categoryId, Long departmentId, Long plantId, Long cityId,
                                        List<String> sizeCategories, List<String> brands, String sortBy,
                                        Pageable pageable, Boolean isFeatured, Boolean isBestSeller,
                                        Boolean isNewArrival, String currency, Double minPrice, Double maxPrice,
                                        String searchString) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            List<Long> filteredProductIds = getFilteredProductIds(plantId, cityId, cb);

            CriteriaQuery<ProductMaster> query = buildProductQuery(cb, filteredProductIds, departmentId, categoryId, brandId,
                    sizeCategories, brands, isFeatured, isBestSeller,
                    isNewArrival, currency, minPrice, maxPrice,
                    searchString, sortBy);

            List<ProductMaster> products = executeProductQuery(query, pageable);

            Long totalCount = countTotalRecords(departmentId, categoryId, brandId, isFeatured, isBestSeller, isNewArrival,
                    currency, minPrice, maxPrice);

            Map<Long, Integer> productQuantities = cityId != null ? loadProductQuantitiesByCity(cityId, products) : Collections.emptyMap();

            List<Product> productDtos = mapToProductDTOs(products, productQuantities);

            return new PageImpl<>(productDtos, pageable, totalCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<Long> getFilteredProductIds(Long plantId, Long cityId, CriteriaBuilder cb) {
        if (plantId == null && cityId == null) {
            return Collections.emptyList();
        }

        CriteriaQuery<Long> subquery = cb.createQuery(Long.class);
        Root<Inventory> inventoryRoot = subquery.from(Inventory.class);
        subquery.select(inventoryRoot.get("product").get("id"));

        List<Predicate> inventoryPredicates = new ArrayList<>();
        if (plantId != null) {
            Join<Inventory, PlantMaster> plantJoin = inventoryRoot.join("plantMaster");
            inventoryPredicates.add(cb.equal(plantJoin.get("id"), plantId));
        }
        if (cityId != null) {
            Join<Inventory, PlantMaster> plantJoin = inventoryRoot.join("plantMaster");
            Join<PlantMaster, CityMaster> cityJoin = plantJoin.join("city");
            inventoryPredicates.add(cb.equal(cityJoin.get("id"), cityId));
        }

        subquery.where(cb.and(inventoryPredicates.toArray(new Predicate[0])));

        return entityManager.createQuery(subquery).getResultList();
    }

    private CriteriaQuery<ProductMaster> buildProductQuery(CriteriaBuilder cb, List<Long> filteredProductIds,
                                                           Long departmentId, Long categoryId, Long brandId,
                                                           List<String> sizeCategories, List<String> brands,
                                                           Boolean isFeatured, Boolean isBestSeller,
                                                           Boolean isNewArrival, String currency, Double minPrice,
                                                           Double maxPrice, String searchString, String sortBy) {
        CriteriaQuery<ProductMaster> query = cb.createQuery(ProductMaster.class);
        Root<ProductMaster> product = query.from(ProductMaster.class);

        List<Predicate> predicates = new ArrayList<>();

        addSubqueryPredicate(filteredProductIds, cb, product, predicates);
        addDepartmentCategoryBrandPredicates(cb, product, departmentId, categoryId, brandId, predicates);
        addSizeCategoryPredicates(cb, product, sizeCategories, predicates);
        addBrandPredicates(brands, cb, product, predicates);
        addFeaturePredicates(cb, product, isFeatured, isBestSeller, isNewArrival, predicates);
        addPriceRangePredicate(cb, product, currency, minPrice, maxPrice, predicates);
        addSearchStringPredicate(cb, product, searchString, predicates);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        addSorting(cb, product, query, sortBy, departmentId, categoryId);

        return query;
    }

    private void addSubqueryPredicate(List<Long> filteredProductIds, CriteriaBuilder cb, Root<ProductMaster> product,
                                      List<Predicate> predicates) {
        if (!filteredProductIds.isEmpty()) {
            predicates.add(product.get("id").in(filteredProductIds));
        } else {
            predicates.add(cb.disjunction());
        }
    }

    private void addDepartmentCategoryBrandPredicates(CriteriaBuilder cb, Root<ProductMaster> product, Long departmentId,
                                                      Long categoryId, Long brandId, List<Predicate> predicates) {
        if (departmentId != null) {
            predicates.add(cb.equal(product.get("department").get("id"), departmentId));
        }
        if (categoryId != null) {
            predicates.add(cb.equal(product.get("category").get("id"), categoryId));
        }
        if (brandId != null) {
            predicates.add(cb.equal(product.get("brand").get("id"), brandId));
        }
    }

    private void addSizeCategoryPredicates(CriteriaBuilder cb, Root<ProductMaster> product, List<String> sizeCategories,
                                           List<Predicate> predicates) {
        if (sizeCategories == null || sizeCategories.isEmpty()) {
            return;
        }

        List<Predicate> sizePredicates = new ArrayList<>();
        for (String sizeCategory : sizeCategories) {
            switch (sizeCategory) {
                case "small":
                    sizePredicates.add(cb.lessThan(product.get("volume"), 0.05));
                    break;
                case "medium":
                    sizePredicates.add(cb.between(product.get("volume"), 0.05, 0.5));
                    break;
                case "large":
                    sizePredicates.add(cb.between(product.get("volume"), 0.5, 0.75));
                    break;
                case "extra_large":
                    sizePredicates.add(cb.greaterThan(product.get("volume"), 0.75));
                    break;
            }
        }
        if (!sizePredicates.isEmpty()) {
            predicates.add(cb.or(sizePredicates.toArray(new Predicate[0])));
        }
    }

    private void addBrandPredicates(List<String> brands, CriteriaBuilder cb, Root<ProductMaster> product,
                                    List<Predicate> predicates) {
        if (brands == null || brands.isEmpty()) {
            return;
        }

        List<BrandMaster> brandName = brandMasterRepo.findByBrandNameIn(brands);
        if (!brandName.isEmpty()) {
            List<Long> brandIds = brandName.stream().map(BrandMaster::getId).collect(Collectors.toList());
            predicates.add(product.get("brand").get("id").in(brandIds));
        }
    }

    private void addFeaturePredicates(CriteriaBuilder cb, Root<ProductMaster> product, Boolean isFeatured,
                                      Boolean isBestSeller, Boolean isNewArrival, List<Predicate> predicates) {
        if (isFeatured != null && isFeatured) {
            predicates.add(cb.isTrue(product.get("isFeatureProduct")));
        }
        if (isBestSeller != null && isBestSeller) {
            predicates.add(cb.isTrue(product.get("isBestSeller")));
        }
        if (isNewArrival != null && isNewArrival) {
            predicates.add(cb.isTrue(product.get("isNewArrival")));
        }
    }

    private void addPriceRangePredicate(CriteriaBuilder cb, Root<ProductMaster> product, String currency, Double minPrice,
                                        Double maxPrice, List<Predicate> predicates) {
        if (currency == null || currency.isEmpty() || minPrice == null || maxPrice == null) {
            return;
        }

        Path<BigDecimal> pricePath;
        if ("USD".equalsIgnoreCase(currency)) {
            pricePath = product.get("regularPriceInDollar");
        } else if ("LKR".equalsIgnoreCase(currency)) {
            pricePath = product.get("regularPriceInRupee");
        } else {
            throw new IllegalArgumentException("Invalid currency value: " + currency);
        }

        predicates.add(cb.between(pricePath, BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice)));
    }

    private void addSearchStringPredicate(CriteriaBuilder cb, Root<ProductMaster> product, String searchString,
                                          List<Predicate> predicates) {
        if (searchString == null || searchString.trim().isEmpty()) {
            return;
        }

        Join<ProductMaster, DepartmentMaster> depJoin = product.join("department", JoinType.LEFT);
        Join<ProductMaster, CategoryMaster> catJoin = product.join("category", JoinType.LEFT);
        Join<ProductMaster, BrandMaster> brandJoin = product.join("brand", JoinType.LEFT);

        String pattern = "%" + searchString.trim().toLowerCase() + "%";
        Predicate nameLike = cb.like(cb.lower(product.get("correctName")), pattern);
        Predicate depLike = cb.like(cb.lower(depJoin.get("departmentName")), pattern);
        Predicate catLike = cb.like(cb.lower(catJoin.get("categoryName")), pattern);
        Predicate brandLike = cb.like(cb.lower(brandJoin.get("brandName")), pattern);

        Predicate searchPredicate = cb.or(nameLike, depLike, catLike, brandLike);
        predicates.add(searchPredicate);
    }

    private void addSorting(CriteriaBuilder cb, Root<ProductMaster> product, CriteriaQuery<ProductMaster> query,
                            String sortBy, Long departmentId, Long categoryId) {
        if (sortBy == null || sortBy.isEmpty()) {
            return;
        }

        switch (sortBy) {
            case "price_low_high":
                query.orderBy(cb.asc(product.get("regularPriceInRupee")));
                break;
            case "price_high_low":
                query.orderBy(cb.desc(product.get("regularPriceInRupee")));
                break;
            case "fast_moving":
                Predicate isFastMoving = cb.isTrue(product.get("isFastMoving"));
                Predicate inDepartment = cb.equal(product.get("department").get("id"), departmentId);
                Predicate inCategory = categoryId != null ? cb.equal(product.get("category").get("id"), categoryId) : null;

                if (inCategory != null) {
                    query.where(cb.and(isFastMoving, inDepartment, inCategory));
                } else {
                    query.where(cb.and(isFastMoving, inDepartment));
                }
                break;
        }
    }

    private List<ProductMaster> executeProductQuery(CriteriaQuery<ProductMaster> query, Pageable pageable) {
        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private Long countTotalRecords(Long departmentId, Long categoryId, Long brandId, Boolean isFeatured,
                                   Boolean isBestSeller, Boolean isNewArrival, String currency, Double minPrice,
                                   Double maxPrice) {
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(p) FROM ProductMaster p WHERE 1 = 1");
        if (departmentId != null) countQueryBuilder.append(" AND p.department.id = :departmentId");
        if (categoryId != null) countQueryBuilder.append(" AND p.category.id = :categoryId");
        if (brandId != null) countQueryBuilder.append(" AND p.brand.id = :brandId");
        if (isFeatured != null && isFeatured) countQueryBuilder.append(" AND p.isFeatureProduct = true");
        if (isBestSeller != null && isBestSeller) countQueryBuilder.append(" AND p.isBestSeller = true");
        if (isNewArrival != null && isNewArrival) countQueryBuilder.append(" AND p.isNewArrival = true");
        if (currency != null && !currency.isEmpty()) {
            if ("USD".equalsIgnoreCase(currency)) {
                countQueryBuilder.append(" AND p.regularPriceInDollar BETWEEN :minPrice AND :maxPrice");
            } else if ("LKR".equalsIgnoreCase(currency)) {
                countQueryBuilder.append(" AND p.regularPriceInRupee BETWEEN :minPrice AND :maxPrice");
            }
        }

        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        if (departmentId != null) countQuery.setParameter("departmentId", departmentId);
        if (categoryId != null) countQuery.setParameter("categoryId", categoryId);
        if (brandId != null) countQuery.setParameter("brandId", brandId);
        if (currency != null && !currency.isEmpty()) {
            countQuery.setParameter("minPrice", minPrice);
            countQuery.setParameter("maxPrice", maxPrice);
        }

        return (Long) countQuery.getSingleResult();
    }

    private List<Product> mapToProductDTOs(List<ProductMaster> products, Map<Long, Integer> productQuantities) {
        return products.stream()
                .map(productMaster -> {
                    Product dto = convertToDto(productMaster);
                    dto.setQuantity(productQuantities.getOrDefault(productMaster.getId(), 0));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> loadProductQuantitiesByCity(Long cityId, List<ProductMaster> products) {
        if (products.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> productIds = products.stream()
                .map(ProductMaster::getId)
                .collect(Collectors.toList());

        String query = "SELECT i.product.id, SUM(i.totalQuantity) " +
                "FROM Inventory i " +
                "JOIN i.plantMaster p " +
                "JOIN p.city c " +
                "WHERE c.id = :cityId AND i.product.id IN :productIds " +
                "GROUP BY i.product.id";

        List<Object[]> results = entityManager.createQuery(query)
                .setParameter("cityId", cityId)
                .setParameter("productIds", productIds)
                .getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> ((Number) result[1]).intValue()
                ));
    }




    @Override
    public List<ProductFilterValues> getProductFilterValues() {
        // Fetch unique combinations with price details
        List<Object[]> results = productMasterRepo.findUniqueDepartmentCategoryBrandCombinationsWithPrices();

        // Process results into hierarchical structure
        Map<Long, ProductFilterValues> departmentMap = new HashMap<>();

        for (Object[] row : results) {
            Long departmentId = ((Number) row[0]).longValue();
            String departmentName = (String) row[1];
            Long categoryId = ((Number) row[2]).longValue();
            String categoryName = (String) row[3];
            Long brandId = ((Number) row[4]).longValue();
            String brandName = (String) row[5];
            Double minPriceDollar = ((Number) row[6]).doubleValue();
            Double maxPriceDollar = ((Number) row[7]).doubleValue();
            Double minPriceRupee = ((Number) row[8]).doubleValue();
            Double maxPriceRupee = ((Number) row[9]).doubleValue();

            // Map department
            ProductFilterValues departmentFilter = departmentMap.computeIfAbsent(
                    departmentId,
                    id -> {
                        ProductFilterValues newFilter = new ProductFilterValues();
                        newFilter.setDepartmertId(departmentId);
                        newFilter.setDepartment(departmentName);
                        newFilter.setCategories(new ArrayList<>());
                        newFilter.setMinPriceInDollar(Double.MAX_VALUE);
                        newFilter.setMaxPriceInDollar(Double.MIN_VALUE);
                        newFilter.setMinPriceInRupee(Double.MAX_VALUE);
                        newFilter.setMaxPriceInRupee(Double.MIN_VALUE);
                        return newFilter;
                    }
            );

            // Update department price range
            departmentFilter.setMinPriceInDollar(Math.min(departmentFilter.getMinPriceInDollar(), minPriceDollar));
            departmentFilter.setMaxPriceInDollar(Math.max(departmentFilter.getMaxPriceInDollar(), maxPriceDollar));
            departmentFilter.setMinPriceInRupee(Math.min(departmentFilter.getMinPriceInRupee(), minPriceRupee));
            departmentFilter.setMaxPriceInRupee(Math.max(departmentFilter.getMaxPriceInRupee(), maxPriceRupee));

            // Map category
            ProductFilterValues.CatrgoryVlaues categoryFilter = departmentFilter.getCategories().stream()
                    .filter(c -> c.getCategoryId().equals(categoryId))
                    .findFirst()
                    .orElseGet(() -> {
                        ProductFilterValues.CatrgoryVlaues newCategory = departmentFilter.new CatrgoryVlaues();
                        newCategory.setCategoryId(categoryId);
                        newCategory.setCatrgory(categoryName);
                        newCategory.setBrands(new ArrayList<>());
                        departmentFilter.getCategories().add(newCategory);
                        return newCategory;
                    });

            // Map brand
            ProductFilterValues.Brandvalues brandFilter = new ProductFilterValues.Brandvalues();
            brandFilter.setBrandId(brandId);
            brandFilter.setBrand(brandName);
            categoryFilter.getBrands().add(brandFilter);
        }

        Optional<ProductFilterValues> gift = departmentMap.values().stream().filter(department -> department.getDepartmertId() == 4).findFirst();
        gift.ifPresent(productFilterValues -> productFilterValues.setCategories(new ArrayList<>()));

        return new ArrayList<>(departmentMap.values());
    }

    @Override
    @Transactional
    public Product upsert(SAPMaterialItem sapMaterialItem) {
        // Check if a ProductMaster entity exists with the given unique identifiers (navItemNo or sapItemNo)
        ProductMaster existingProductMaster = repository.findByMaterialOrNavItemCode(
                sapMaterialItem.getSapItemNo(),
                sapMaterialItem.getNavItemNo()
        );

        ProductMaster productMaster;
        if (existingProductMaster != null) {
            // Update the existing ProductMaster
            productMaster = existingProductMaster;
        } else {
            // Create a new ProductMaster
            productMaster = new ProductMaster();
        }

        // Map fields from SAPMaterialItem to ProductMaster
        productMaster.setMaterial(sapMaterialItem.getSapItemNo());
        productMaster.setNavItemCode(sapMaterialItem.getNavItemNo());
        productMaster.setMaterialDescription(sapMaterialItem.getDescription());
        productMaster.setNewName(sapMaterialItem.getDescription2());
        productMaster.setBaseUnitOfMeasure(sapMaterialItem.getBaseUom());
        productMaster.setMaterialType(sapMaterialItem.getType());
        productMaster.setVolume(sapMaterialItem.getUnitVolume() != null ? BigDecimal.valueOf(sapMaterialItem.getUnitVolume()) : null);
        productMaster.setMaterialGroup(sapMaterialItem.getItemCatCode());
        productMaster.setMaterialGroup1(sapMaterialItem.getProductGrpCode());
        productMaster.setMaterialGroup2(sapMaterialItem.getProductSubGrp1());
        productMaster.setMaterialGroup3(sapMaterialItem.getProductSubGrp2());
        productMaster.setWwProductRef(sapMaterialItem.getWebReferenceNo());
        productMaster.setVolumeUnit(sapMaterialItem.getSalesUom());
        productMaster.setWeightUnit(sapMaterialItem.getPurchUom());
        
        //received value "1" for Blocked "0" for enabled
        String blocked = sapMaterialItem.getBlocked();
        productMaster.setIsEnabled(blocked != null && !blocked.trim().isEmpty() && "0".equalsIgnoreCase(blocked.trim())); // Enable if not blocked

        // Save the updated or new ProductMaster entity
        ProductMaster savedProductMaster = repository.save(productMaster);

        // Convert and return the DTO
        return convertToDto(savedProductMaster);
    }



    @Override
    public List<DepartmentCategoryProductDTO> getDepartmentCategoryProductHierarchy(Long cityId) {
        String cacheKey = "productHierarchy:" + cityId;
        return hierarchyCache.get(cacheKey, () -> {
            List<Object[]> rows = productMasterRepo.findAllProductHierarchyDataByCity(cityId);

            Set<Long> seenDepartments = new HashSet<>();
            Set<String> seenCategories = new HashSet<>();

            List<DepartmentCategoryProductDTO> result = new ArrayList<>();

            for (Object[] row : rows) {
                Long depId = ((Number) row[0]).longValue();
                String depName = (String) row[1];
                Long catId = ((Number) row[2]).longValue();
                String catName = (String) row[3];
                Long productId = ((Number) row[4]).longValue();
                String productName = (String) row[5]; // correctName
                String productImageUrl = (String) row[6];

                // Department level
                if (seenDepartments.add(depId)) {
                    DepartmentCategoryProductDTO depDTO = new DepartmentCategoryProductDTO();
                    depDTO.setStr(depName);
                    depDTO.setDep(depId);
                    depDTO.setCat(null);
                    depDTO.setProd(null);
                    depDTO.setImageLink(null);
                    depDTO.setSrch(depName); // srch same as department name
                    result.add(depDTO);
                }

                // Category level
                String depCatKey = depId + "-" + catId;
                if (seenCategories.add(depCatKey)) {
                    DepartmentCategoryProductDTO catDTO = new DepartmentCategoryProductDTO();
                    catDTO.setStr(catName);
                    catDTO.setDep(depId);
                    catDTO.setCat(catId);
                    catDTO.setProd(null);
                    catDTO.setImageLink(null);
                    catDTO.setSrch(catName); // srch same as category name
                    result.add(catDTO);
                }

                // Product level
                DepartmentCategoryProductDTO prodDTO = new DepartmentCategoryProductDTO();
                prodDTO.setStr(productName);
                prodDTO.setDep(depId);
                prodDTO.setCat(catId);
                prodDTO.setProd(productId);
                prodDTO.setImageLink(productImageUrl != null ? productImageUrl : null);
                // srch = productName + " " + depName + " " + catName
                prodDTO.setSrch(productName + " " + depName + " " + catName);
                result.add(prodDTO);
            }
            return result;
        });
    }

    @Override
    public Product findByCode(String code) {
        ProductMaster entity = repository.findByUrlCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Requested resource with code: " + code + " does not exist."));

        return convertToDto(entity);
    }

    // Optional: If you need to invalidate cache for a city
    public void invalidateProductHierarchyCache(Long cityId) {
        hierarchyCache.invalidate("productHierarchy:" + cityId);
    }


    @Override
    public Product converttoDto(ProductMaster productMaster) {
        return modelMapper.map(productMaster, Product.class);
    }

    @Override
    public ProductMaster convertToEntity(Product dto) {
        return modelMapper.map(dto, ProductMaster.class);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //runs daily at midnight
    public void updateProductPrices() {

        //get the active selling prices
        List<SellingPriceMaster> activePrices = sellingPriceMasterRepo.findByIsActiveTrue();

        //fetch the latest exchange rate
        Double exchangeRate = exchangeRateMasterRepo
                .findTopByFromCurrencyAndToCurrencyOrderByStartDateDesc("LKR", "USD")
                .map(ExchangeRateMaster::getExRate)
                .orElseThrow(() -> new RuntimeException("Exchange rate not found"));

        //update product prices
        for (SellingPriceMaster price : activePrices){
            productMasterRepo.findByMaterial(price.getSapMaterialCode()).ifPresent(product -> {
                BigDecimal lkrPrice = BigDecimal.valueOf(price.getUnitPrice());
                BigDecimal usdPrice = lkrPrice.divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP);

                product.setRegularPriceInRupee(lkrPrice);
                product.setRegularPriceInDollar(usdPrice);

                productMasterRepo.save(product);

                System.out.println("Updated product: " + product.getMaterial() +
                        ", LKR: " + lkrPrice + ", USD: " + usdPrice);
            });
        }

        //Update DeliveryChargesMaster Prices
        List<DeliveryChargesMaster> deliveryCharges = deliverChargesRepo.findAll();
        for (DeliveryChargesMaster charge : deliveryCharges){
            if (charge.getBasePriceInRupee() != null) {
                charge.setBasePriceInDollar(charge.getBasePriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getTwoBottlesPriceInRupee() != null) {
                charge.setTwoBottlesPriceInDollar(charge.getTwoBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getThreeBottlesPriceInRupee() != null) {
                charge.setThreeBottlesPriceInDollar(charge.getThreeBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getFourBottlesPriceInRupee() != null) {
                charge.setFourBottlesPriceInDollar(charge.getFourBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getFiveBottlesPriceInRupee() != null) {
                charge.setFiveBottlesPriceInDollar(charge.getFiveBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getSixBottlesPriceInRupee() != null) {
                charge.setSixBottlesPriceInDollar(charge.getSixBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getSevenBottlesPriceInRupee() != null) {
                charge.setSevenBottlesPriceInDollar(charge.getSevenBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getEightBottlesPriceInRupee() != null) {
                charge.setEightBottlesPriceInDollar(charge.getEightBottlesPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }
            if (charge.getComboPackInRupee() != null) {
                charge.setComboPackInDollar(charge.getComboPackInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            }

            deliverChargesRepo.save(charge);

            System.out.println("Updated delivery charges: " +
                    ", Base Price: " + charge.getBasePriceInRupee() + ", USD: " + charge.getBasePriceInDollar());
        }

        //update addon prices
        List<Addon> addons = addonRepo.findAll();
        for (Addon addon : addons) {
            addon.setPriceInDollar(addon.getPriceInRupee().divide(BigDecimal.valueOf(exchangeRate), 2, RoundingMode.HALF_UP));
            addonRepo.save(addon);

            System.out.println("Updated addon: " + addon.getName() +
                    ", LKR: " + addon.getPriceInRupee() + ", USD: " + addon.getPriceInDollar());
        }
        System.out.println("Product prices updated successfully.");

    }

}
