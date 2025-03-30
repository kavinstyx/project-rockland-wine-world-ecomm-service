package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMasterRepo extends JpaRepository<ProductMaster, Long> {

    @Query("SELECT DISTINCT p.brand.brandName " +
            "FROM ProductMaster p " +
            "WHERE (:departmentId IS NULL OR p.department.id = :departmentId) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    List<String> findBrandNamesByDepartmentAndCategory(
            @Param("departmentId") Long departmentId,
            @Param("categoryId") Long categoryId);
    @Query("SELECT DISTINCT p.brand.brandName FROM ProductMaster p")
    List<String> findAllBrandNames();

    @Query("SELECT DISTINCT p.sku FROM ProductMaster p")
    List<String> findAllSku();

    ProductMaster findByMaterialOrNavItemCode(String sapItemNo, String navItemNo);

    @Query(value = """
        SELECT DISTINCT
            dm.id AS departmentId, dm.department_name AS departmentName,
            cm.id AS categoryId, cm.category_name AS categoryName,
            bm.id AS brandId, bm.brand_name AS brandName,
            MIN(pm.regular_price_dollar) AS minPriceDollar,
            MAX(pm.regular_price_dollar) AS maxPriceDollar,
            MIN(pm.regular_price_rupee) AS minPriceRupee,
            MAX(pm.regular_price_rupee) AS maxPriceRupee
        FROM product_master pm
        JOIN department_master dm ON pm.department_id = dm.id
        JOIN category_master cm ON pm.category_id = cm.id
        JOIN brand_master bm ON pm.brand_id = bm.id
        WHERE pm.department_id IS NOT NULL AND pm.category_id IS NOT NULL AND pm.brand_id IS NOT NULL
        GROUP BY dm.id, dm.department_name, cm.id, cm.category_name, bm.id, bm.brand_name
        """, nativeQuery = true)
    List<Object[]> findUniqueDepartmentCategoryBrandCombinationsWithPrices();

    @Query(value = """
    SELECT dm.id AS department_id,
           dm.department_name AS department_name,
           cm.id AS category_id,
           cm.category_name AS category_name,
           pm.id AS product_id,
           pm.correct_name AS correct_name,
           pm.image_url AS image_url
    FROM product_master pm
    JOIN department_master dm ON pm.department_id = dm.id
    JOIN category_master cm ON pm.category_id = cm.id
    JOIN inventory i ON i.product_id = pm.id
    JOIN plant_master pl ON i.plant_id = pl.id
    WHERE pl.city_id = :cityId
      AND i.total_quantity > 0
    GROUP BY dm.id, dm.department_name, cm.id, cm.category_name, pm.id, pm.correct_name, pm.image_url
    ORDER BY dm.department_name, cm.category_name, pm.correct_name
    """, nativeQuery = true)
    List<Object[]> findAllProductHierarchyDataByCity(@Param("cityId") Long cityId);


    Optional<ProductMaster> findByMaterial(String material);
    Optional<ProductMaster> findByUrlCode(String productCode);

    @Query("SELECT DISTINCT p.correctName FROM ProductMaster p")
    List<String> findAllCorrectNames();

}
