package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.DeliveryCharges;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.service.DeliveryChargeMasterService;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("api/delivery-charges")
@CrossOrigin

public class DeliveryChargesMasterController extends AbstractCrudController<DeliveryCharges, Long, DeliveryChargeMasterService> {

    private final DeliveryChargeMasterService deliveryChargeMasterService;


    protected DeliveryChargesMasterController(DeliveryChargeMasterService service, DeliveryChargeMasterService deliveryChargeMasterService) {
        super(service);
        this.deliveryChargeMasterService = deliveryChargeMasterService;
    }

    @GetMapping("/calculateDeliveryCharge")
    public ResponseEntity<Response<DeliveryChargeResponse>> calculateDeliveryCharge(
            @RequestParam("bottleQuantity") int bottleQuantity,
            @RequestParam("cityId") Long cityId) {
        try {
            if (bottleQuantity <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Response.<DeliveryChargeResponse>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .status(Status.ERROR)
                                .message("Bottle quantity must be greater than 0")
                                .build());
            }

            DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(bottleQuantity, cityId);

            return ResponseEntity.ok(
                    Response.<DeliveryChargeResponse>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("Delivery charge calculated successfully")
                            .data(deliveryChargeResponse)
                            .build()
            );
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<DeliveryChargeResponse>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message(ex.getMessage())
                            .build());
        } catch (Exception ex) {
            log.error("Error calculating delivery charge: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<DeliveryChargeResponse>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message("An unexpected error occurred while calculating delivery charges")
                            .build());
        }
    }

}

