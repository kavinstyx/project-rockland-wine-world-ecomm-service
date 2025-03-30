package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Product;
import com.commonlibrary.contract.v1.sap.SAPMaterialItem;
import com.commonlibrary.contract.v1.ProductFilterValues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rockland.elysiancrest.com.data_service.dto.DepartmentCategoryProductDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

import java.util.List;

public interface ProductMasterService extends CrudService<Product, Long>{
    Product converttoDto(ProductMaster productMaster);
    ProductMaster convertToEntity(Product dto);

    Page<Product> searchProducts(Long brandId, Long categoryId, Long departmentId, Long plantId, Long cityId, List<String> sizeCategories, List<String> brands, String sortBy, Pageable pageable, Boolean isFeatured, Boolean isBestSeller, Boolean isNewArrival, String currency, Double minPrice, Double maxPrice, String searchString);
    List<ProductFilterValues> getProductFilterValues();

    List<DepartmentCategoryProductDTO> getDepartmentCategoryProductHierarchy(Long cityId);

    Product findByCode(String code);
    Product upsert(SAPMaterialItem sapMaterialItem);

    void updateProductPrices();
}
