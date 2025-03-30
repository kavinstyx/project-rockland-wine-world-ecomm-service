package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.SellingPrice;
import com.commonlibrary.contract.v1.sap.SAPMaterialPricing;
import rockland.elysiancrest.com.data_service.dto.SellingPriceMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.SellingPriceMaster;

public interface SellingPriceMaserService extends CrudService<SellingPrice, Long>{
//    SellingPriceMasterDTO convertToDto(SellingPriceMaster sellingPriceMaster);
//    SellingPriceMaster convertToEntity(SellingPriceMasterDTO dto);

    SellingPrice upsert(SAPMaterialPricing sapMaterialPricing);
}
