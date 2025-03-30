package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.CategoryMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.CategoryMaster;
import rockland.elysiancrest.com.data_service.service.CategoryMasterService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/category")
@CrossOrigin
public class CategoryMasterController extends AbstractCrudController<Category, Long, CategoryMasterService>{
    protected CategoryMasterController(CategoryMasterService service){
        super(service);
    }
}

//    private final CategoryMasterService categoryMasterService;
//    private final Logger logger = Logger.getLogger("msg-data-service");
//
//    public CategoryMasterController(CategoryMasterService categoryMasterService) {
//        this.categoryMasterService = categoryMasterService;
//    }
//
//    @ExceptionHandler({Exception.class})
//    public String databaseError(Exception e) {
//        e.printStackTrace();
//        return "databaseError";
//    }
//
//    @PostMapping
//    public ResponseEntity<CategoryMasterDTO> createCategory(@RequestBody CategoryMasterDTO dto) {
//        logger.info("Entering createCategory method");
//        CategoryMaster categoryMaster = categoryMasterService.convertToEntity(dto);
//        CategoryMaster savedCategory = categoryMasterService.save(categoryMaster);
//
//        // Convert the saved entity back to DTO for response
//        CategoryMasterDTO responseDto = categoryMasterService.convertToDto(savedCategory);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryMasterDTO> getCategoryById(@PathVariable("id") Long id) {
//        logger.info("Entering getCategoryById method");
//        return categoryMasterService.findById(id)
//                .map(category -> {
//                    CategoryMasterDTO responseDto = categoryMasterService.convertToDto(category);
//                    System.out.println("CategoryMasterDTO: " + responseDto.toString());
//                    return ResponseEntity.ok(responseDto);
//                })
//                .orElseGet(() -> {
//                    System.out.println("Category not found for id: " + id);
//                    return ResponseEntity.notFound().build();
//                });
//    }
//
//    @GetMapping("")
//    public ResponseEntity<Page<CategoryMasterDTO>> getAllCategories(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size) {
//        try {
//            logger.info("Entering getAllCategories method");
//            PageRequest pageRequest = PageRequest.of(page, size);
//            Page<CategoryMaster> categoryPage = categoryMasterService.findAll(pageRequest);
//
//            // Convert Page<CategoryMaster> to Page<CategoryMasterDTO>
//            Page<CategoryMasterDTO> categoryDtoPage = categoryPage.map(categoryMasterService::convertToDto);
//            return ResponseEntity.ok(categoryDtoPage);
//        } catch (Exception e) {
//            logger.info("Error in getAllCategories method: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//    @PutMapping("/put/{id}")
//    public ResponseEntity<CategoryMasterDTO> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryMasterDTO dto) {
//        if (!categoryMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        CategoryMaster categoryMaster = categoryMasterService.convertToEntity(dto);
//        CategoryMaster updatedCategory = categoryMasterService.update(id, categoryMaster);
//        return ResponseEntity.ok(categoryMasterService.convertToDto(updatedCategory));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
//        if (!categoryMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        categoryMasterService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
