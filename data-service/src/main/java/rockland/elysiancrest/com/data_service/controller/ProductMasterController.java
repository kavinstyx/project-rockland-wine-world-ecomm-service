package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Product;
import com.commonlibrary.contract.v1.ProductFilterValues;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.DepartmentCategoryProductDTO;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.repo.CityMasterRepo;
import rockland.elysiancrest.com.data_service.repo.FavoriteItemRepository;
import rockland.elysiancrest.com.data_service.repo.InventoryRepo;
import rockland.elysiancrest.com.data_service.service.ProductMasterService;


import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/products")
@CrossOrigin
public class ProductMasterController extends AbstractCrudController<Product, Long, ProductMasterService> {

    private final InventoryRepo inventoryRepo;
    private final CityMasterRepo cityMasterRepo;
    private ProductMasterService productMasterService;
    private final Logger logger = Logger.getLogger("msg-data-service");
    private final FavoriteItemRepository favoriteItemRepository;

    protected ProductMasterController(ProductMasterService service, FavoriteItemRepository favoriteItemRepository, InventoryRepo inventoryRepo, CityMasterRepo cityMasterRepo) {
        super(service);
        this.favoriteItemRepository = favoriteItemRepository;
        logger.info("product controller initialized");
        this.productMasterService = service;
        this.inventoryRepo = inventoryRepo;
        this.cityMasterRepo = cityMasterRepo;
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<List<DepartmentCategoryProductDTO>> getDepartmentCategoryProductHierarchy(
            @RequestParam(name = "cityId") Long cityId) {

        List<DepartmentCategoryProductDTO> hierarchy = service.getDepartmentCategoryProductHierarchy(cityId);
        return ResponseEntity.ok(hierarchy);
    }


    @GetMapping("/search-products")
    @Operation(summary = "Search products")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "plantId", required = false) Long plantId,
            @RequestParam(name = "cityId", required = false) Long cityId,
            @RequestParam(name = "sizeCategories", defaultValue = "0", required = false) List<String> sizeCategories,// Values: "small", "medium", "large", "extra_large"
            @RequestParam(name = "brands", defaultValue = "0", required = false) List<String> brands,
            @RequestParam(name = "sortBy", defaultValue = "0", required = false) String sortBy,  // Values: "price_low_high", "price_high_low", "fast_moving"
            @RequestParam(name = "isFeatured", required = false) boolean isFeatured,
            @RequestParam(name = "isNewArrival", required = false) boolean isNewArrival,
            @RequestParam(name = "isBestSeller", required = false) boolean isBestSeller,
            @RequestParam(name = "currency", required = false) String currency,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "searchString", required = false) String searchString

    ) {
        try {

            PageRequest pageRequest = PageRequest.of(page, size);

            Page<Product> productPage = productMasterService.searchProducts(brandId, categoryId, departmentId, plantId, cityId, sizeCategories, brands, sortBy, pageRequest, isFeatured, isBestSeller, isNewArrival, currency, minPrice, maxPrice, searchString);

            return ResponseEntity.ok(productPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Response<Product>> findResource(@PathVariable String code) {
        return ResponseEntity.ok(Response.<Product>builder().code(HttpStatus.OK.value()).status(Status.SUCCESS)
                .data(service.findByCode(code)).build());
    }


    @GetMapping("/filter-values")
    public ResponseEntity<List<ProductFilterValues>> getProductFilterValues() {
        List<ProductFilterValues> filterValues = productMasterService.getProductFilterValues();
        return ResponseEntity.ok(filterValues);
    }

    @GetMapping("singleProduct/{id}")
    public ResponseEntity<Response<Product>> findResource(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam Long cityId) {

        // Fetch product details
        Product productDTO = service.findById(id);

        // Check if the product is a favorite for the user
        boolean isFavorite = (userId != null) && favoriteItemRepository.existsByUserIdAndProductId(userId, id);
        productDTO.setFavorite(isFavorite);

        Optional<CityMaster> city = cityMasterRepo.findById(cityId);
        if (city.isEmpty()) {
            throw new ResourceNotFoundException("City not found for product ID: " + id);
        }

        // Fetch inventory quantity for the product
        Inventory inventory = inventoryRepo.findByProductIdAndPlantMasterId(id, city.get().getPlants().get(0).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product ID: " + id));
        productDTO.setQuantity(inventory.getTotalQuantity() - inventory.getReservedQuantity() - inventory.getBufferQuantity());

        // Build and return the response
        return ResponseEntity.ok(Response.<Product>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(productDTO)
                .build());
    }


    @PostMapping("/update_price")
    public ResponseEntity<Response<String>> updatePrices() {
        productMasterService.updateProductPrices();

        return ResponseEntity.ok(Response.<String>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("Prices updated sucessfully!")
                .data(null)
                .build());
    }

}


