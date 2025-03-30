package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.BrandMasterDTO;
import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.BrandMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.service.BrandMasterService;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/brands")
@CrossOrigin
public class BrandMasterController extends AbstractCrudController<Brand, Long, BrandMasterService>{
    protected BrandMasterController(BrandMasterService service) {

        super(service);
    }

    @GetMapping("/brandList/")
    public ResponseEntity<?> getBrandNames(
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "departmentId", required = false) Long departmentId){

        List<String> brandNames;
        if (categoryId == null && departmentId == null) {
            // Fetch all brand names
            brandNames = service.getAllBrandNames();

        } else if (categoryId != null || departmentId != null) {
            // Fetch brand names for specific category or department
            brandNames = service.getBrandNames(departmentId, categoryId);
        } else {
            return ResponseEntity.badRequest().body("Both categoryId and departmentId must be provided together.");
        }
        return ResponseEntity.ok(brandNames);
    }
}

