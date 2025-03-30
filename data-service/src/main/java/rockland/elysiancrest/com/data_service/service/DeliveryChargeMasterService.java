package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.DeliveryCharges;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;

public interface DeliveryChargeMasterService extends CrudService<DeliveryCharges, Long> {
//    DeliverChagesMasterDTO convertToDto(DeliveryChargesMaster deliveryChargesMaster);
//    DeliveryChargesMaster convertToEntity(DeliverChagesMasterDTO dto);

    DeliveryChargesMaster findByCityId(Long cityId);

    public DeliveryChargeResponse calculateDeliveryCharge(int bottleQuantity, Long cityId);

}
