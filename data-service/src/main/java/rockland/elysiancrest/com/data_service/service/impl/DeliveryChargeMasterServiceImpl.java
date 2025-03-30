package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.DeliveryCharges;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;
import rockland.elysiancrest.com.data_service.repo.DeliverChargesRepo;
import rockland.elysiancrest.com.data_service.service.DeliveryChargeMasterService;

import java.math.BigDecimal;

@Service
@Transactional
public class DeliveryChargeMasterServiceImpl extends CrudServiceImpl<DeliveryChargesMaster, Long, DeliverChargesRepo, DeliveryCharges> implements DeliveryChargeMasterService {

    public DeliveryChargeMasterServiceImpl(DeliverChargesRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);

    }

    @Override
    protected DeliveryCharges convertToDto(DeliveryChargesMaster entity) {
        DeliveryCharges dto = super.convertToDto(entity);

        //set city id manually
        if (entity.getCityMaster() != null) {
            dto.setCityId(entity.getCityMaster().getId());

        }

        return dto;
    }

    @Override
    public DeliveryChargesMaster findByCityId(Long cityId) {
        return repository.findByCityMasterId(cityId);
    }

    @Override
    public DeliveryChargeResponse calculateDeliveryCharge(int bottleQuantity, Long cityId) {
        DeliveryChargesMaster deliveryChargesMaster = this.findByCityId(cityId);

        if (deliveryChargesMaster == null) {
            // Log the error for debugging purposes
            throw new IllegalArgumentException("Delivery charges not found for city ID: " + cityId);
        }

        int fullDeliveries = bottleQuantity / 8; // Number of full batches (8 bottles each)
        int remainingBottles = bottleQuantity % 8; // Remaining bottles after full batches

        BigDecimal totalDeliveryChargeInRupee = BigDecimal.ZERO;
        BigDecimal totalDeliveryChargeInDollar = BigDecimal.ZERO;

        // Add charges for each full batch of 8 bottles
        for (int i = 0; i < fullDeliveries; i++) {
            totalDeliveryChargeInRupee = totalDeliveryChargeInRupee.add(
                    deliveryChargesMaster.getEightBottlesPriceInRupee());
            totalDeliveryChargeInDollar = totalDeliveryChargeInDollar.add(
                    deliveryChargesMaster.getEightBottlesPriceInDollar());
        }

        // Add charges for the remaining bottles (less than 8 bottles)
        if (remainingBottles > 0) {
            BigDecimal additionalChargeInRupee = getChargeForRemainingBottles(remainingBottles, deliveryChargesMaster, "Rupee");
            BigDecimal additionalChargeInDollar = getChargeForRemainingBottles(remainingBottles, deliveryChargesMaster, "Dollar");

            totalDeliveryChargeInRupee = totalDeliveryChargeInRupee.add(additionalChargeInRupee);
            totalDeliveryChargeInDollar = totalDeliveryChargeInDollar.add(additionalChargeInDollar);
        }

        return new DeliveryChargeResponse(totalDeliveryChargeInRupee, totalDeliveryChargeInDollar);
    }

    private BigDecimal getChargeForRemainingBottles(int remainingBottles, DeliveryChargesMaster deliveryChargesMaster, String currency) {
        switch (remainingBottles) {
            case 1:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getBasePriceInRupee()
                        : deliveryChargesMaster.getBasePriceInDollar();
            case 2:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getTwoBottlesPriceInRupee()
                        : deliveryChargesMaster.getTwoBottlesPriceInDollar();
            case 3:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getThreeBottlesPriceInRupee()
                        : deliveryChargesMaster.getThreeBottlesPriceInDollar();
            case 4:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getFourBottlesPriceInRupee()
                        : deliveryChargesMaster.getFourBottlesPriceInDollar();
            case 5:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getFiveBottlesPriceInRupee()
                        : deliveryChargesMaster.getFiveBottlesPriceInDollar();
            case 6:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getSixBottlesPriceInRupee()
                        : deliveryChargesMaster.getSixBottlesPriceInDollar();
            case 7:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getSevenBottlesPriceInRupee()
                        : deliveryChargesMaster.getSevenBottlesPriceInDollar();
            case 8:
                return "Rupee".equals(currency)
                        ? deliveryChargesMaster.getEightBottlesPriceInRupee()
                        : deliveryChargesMaster.getEightBottlesPriceInDollar();
            default:
                throw new IllegalArgumentException("Invalid number of bottles: " + remainingBottles);
        }
    }

}
