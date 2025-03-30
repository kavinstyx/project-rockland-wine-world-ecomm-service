package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.SellingPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.SellingPriceMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.SellingPriceMaster;
import rockland.elysiancrest.com.data_service.service.SellingPriceMaserService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/selling-prices")
@CrossOrigin
public class SellingPriceMasterController extends AbstractCrudController<SellingPrice, Long, SellingPriceMaserService> {

    protected SellingPriceMasterController(SellingPriceMaserService service) {
        super(service);
    }
}

//    private final SellingPriceMaserService sellingPriceMaserService;
//    private final Logger logger = Logger.getLogger("msg-data-service");
//
//    public SellingPriceMasterController(SellingPriceMaserService sellingPriceMaserService) {
//        this.sellingPriceMaserService = sellingPriceMaserService;
//        logger.info("Sellingprice controller initialized");  // Test logging here
//
//    }
//
//    @ExceptionHandler({Exception.class})
//    public String databaseError(Exception e) {
//        e.printStackTrace();
//        return "databaseError";
//    }
//
//    @PostMapping
//    public ResponseEntity<SellingPriceMasterDTO> createSellingPrice(@RequestBody SellingPriceMasterDTO dto) {
//        logger.info("Entering createSellingPrice method");
//        SellingPriceMaster sellingPriceMaster = sellingPriceMaserService.convertToEntity(dto);
//        SellingPriceMaster savedSellingPrice = sellingPriceMaserService.save(sellingPriceMaster);
//
//        SellingPriceMasterDTO responseDto = sellingPriceMaserService.convertToDto(savedSellingPrice);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/get/{id}")
//    public ResponseEntity<SellingPriceMasterDTO> getSellingPriceById(@PathVariable("id") Long id) {
//        logger.info("Entering getSellingPriceById method");
//        return sellingPriceMaserService.findById(id)
//                .map(price -> ResponseEntity.ok(sellingPriceMaserService.convertToDto(price)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/getAll")
//    public ResponseEntity<Page<SellingPriceMasterDTO>> getAllSellingPrices(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size) {
//        try {
//            logger.info("Entering getAllSellingPrices method");
//            PageRequest pageRequest = PageRequest.of(page, size);
//            Page<SellingPriceMaster> pricePage = sellingPriceMaserService.findAll(pageRequest);
//
//            Page<SellingPriceMasterDTO> priceDtoPage = pricePage.map(sellingPriceMaserService::convertToDto);
//            return ResponseEntity.ok(priceDtoPage);
//        } catch (Exception e) {
//            logger.info("Error in getAllSellingPrices method: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//    @PutMapping("/put/{id}")
//    public ResponseEntity<SellingPriceMasterDTO> updateSellingPrice(@PathVariable("id") Long id, @RequestBody SellingPriceMasterDTO dto) {
//        if (!sellingPriceMaserService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        SellingPriceMaster sellingPriceMaster = sellingPriceMaserService.convertToEntity(dto);
//        SellingPriceMaster updatedSellingPrice = sellingPriceMaserService.update(id, sellingPriceMaster);
//        return ResponseEntity.ok(sellingPriceMaserService.convertToDto(updatedSellingPrice));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteSellingPrice(@PathVariable("id") Long id) {
//        if (!sellingPriceMaserService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        sellingPriceMaserService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
