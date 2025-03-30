package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.DepartmentMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.DepartmentMaster;
import rockland.elysiancrest.com.data_service.service.DepartmentMasterService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentMasterController extends AbstractCrudController<Department, Long, DepartmentMasterService>{
    protected DepartmentMasterController(DepartmentMasterService service){
        super(service);
    }
}

//    private final DepartmentMasterService departmentMasterService;
//    private final Logger logger = Logger.getLogger("msg-data-service");
//
//    public DepartmentMasterController(DepartmentMasterService departmentMasterService) {
//        this.departmentMasterService = departmentMasterService;
//    }
//
//    @ExceptionHandler({Exception.class})
//    public String databaseError(Exception e) {
//        e.printStackTrace();
//        return "databaseError";
//    }
//
//    @PostMapping
//    public ResponseEntity<DepartmentMasterDTO> createDepartment(@RequestBody DepartmentMasterDTO dto) {
//        logger.info("Entering createDepartment method");
//        DepartmentMaster departmentMaster = departmentMasterService.convertToEntity(dto);
//        DepartmentMaster savedDepartment = departmentMasterService.save(departmentMaster);
//
//        // Convert the saved entity back to DTO for response
//        DepartmentMasterDTO responseDto = departmentMasterService.convertToDto(savedDepartment);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/get/{id}")
//    public ResponseEntity<DepartmentMasterDTO> getDepartmentById(@PathVariable("id") Long id) {
//        logger.info("Entering getDepartmentById method");
//        return departmentMasterService.findById(id)
//                .map(department -> ResponseEntity.ok(departmentMasterService.convertToDto(department)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("")
//    public ResponseEntity<Page<DepartmentMasterDTO>> getAllDepartments(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size) {
//
//        try {
//            logger.info("Entering getAllDepartments method");
//            PageRequest pageRequest = PageRequest.of(page, size);
//            Page<DepartmentMaster> departmentPage = departmentMasterService.findAll(pageRequest);
//
//            // Convert Page<DepartmentMaster> to Page<DepartmentMasterDTO>
//            Page<DepartmentMasterDTO> departmentDtoPage = departmentPage.map(departmentMasterService::convertToDto);
//            return ResponseEntity.ok(departmentDtoPage);
//        } catch (Exception e) {
//            logger.info("Error in getAllDepartments method: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//    @PutMapping("/put/{id}")
//    public ResponseEntity<DepartmentMasterDTO> updateDepartment(@PathVariable("id") Long id, @RequestBody DepartmentMasterDTO dto) {
//        if (!departmentMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        DepartmentMaster departmentMaster = departmentMasterService.convertToEntity(dto);
//        DepartmentMaster updatedDepartment = departmentMasterService.update(id, departmentMaster);
//        return ResponseEntity.ok(departmentMasterService.convertToDto(updatedDepartment));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") Long id) {
//        if (!departmentMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        departmentMasterService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
