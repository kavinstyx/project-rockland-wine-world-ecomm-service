package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Plant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.PlantMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.PlantMaster;
import rockland.elysiancrest.com.data_service.service.PlantMasterService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/plants")
@CrossOrigin
public class PlantMasterController extends AbstractCrudController<Plant, Long, PlantMasterService>{

    protected PlantMasterController(PlantMasterService service){
        super(service);
    }
}

//    private final PlantMasterService plantMasterService;
//    private final Logger logger = Logger.getLogger("msg-data-service");
//
//    public PlantMasterController(PlantMasterService plantMasterService) {
//        this.plantMasterService = plantMasterService;
//    }
//
//    @ExceptionHandler({Exception.class})
//    public String handleException(Exception e) {
//        e.printStackTrace();
//        return "databaseError";
//    }
//
//    @PostMapping
//    public ResponseEntity<PlantMasterDTO> createPlant(@RequestBody PlantMasterDTO dto) {
//        logger.info("Entering createPlant method");
//        PlantMaster plantMaster = plantMasterService.convertToEntity(dto);
//        PlantMaster savedPlant = plantMasterService.save(plantMaster);
//        PlantMasterDTO responseDto = plantMasterService.convertToDto(savedPlant);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/get/{id}")
//    public ResponseEntity<PlantMasterDTO> getPlantById(@PathVariable("id") Long id) {
//        logger.info("Entering getPlantById method");
//        return plantMasterService.findById(id)
//                .map(plant -> ResponseEntity.ok(plantMasterService.convertToDto(plant)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("getAll")
//    public ResponseEntity<Page<PlantMasterDTO>> getAllPlants(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size) {
//        try {
//            logger.info("Entering getAllPlants method");
//            PageRequest pageRequest = PageRequest.of(page, size);
//            Page<PlantMaster> plantPage = plantMasterService.findAll(pageRequest);
//            Page<PlantMasterDTO> plantDtoPage = plantPage.map(plantMasterService::convertToDto);
//            return ResponseEntity.ok(plantDtoPage);
//        } catch (Exception e) {
//            logger.severe("Error in getAllPlants method: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//    @PutMapping("/put/{id}")
//    public ResponseEntity<PlantMasterDTO> updatePlant(@PathVariable("id") Long id, @RequestBody PlantMasterDTO dto) {
//        if (!plantMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        PlantMaster plantMaster = plantMasterService.convertToEntity(dto);
//        PlantMaster updatedPlant = plantMasterService.update(id, plantMaster);
//        return ResponseEntity.ok(plantMasterService.convertToDto(updatedPlant));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deletePlant(@PathVariable("id") Long id) {
//        if (!plantMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        plantMasterService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
