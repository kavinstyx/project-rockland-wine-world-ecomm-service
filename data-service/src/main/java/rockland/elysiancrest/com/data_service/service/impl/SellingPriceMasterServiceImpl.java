package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.SellingPrice;
import com.commonlibrary.contract.v1.sap.SAPMaterialPricing;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.SellingPriceMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.SellingPriceMaster;
import rockland.elysiancrest.com.data_service.repo.SellingPriceMasterRepo;
import rockland.elysiancrest.com.data_service.service.SellingPriceMaserService;

import java.time.LocalDate;

@Service
@Transactional

public class SellingPriceMasterServiceImpl extends CrudServiceImpl<SellingPriceMaster,Long, SellingPriceMasterRepo, SellingPrice> implements SellingPriceMaserService {


    public SellingPriceMasterServiceImpl(SellingPriceMasterRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);

    }

    @Override
    @Transactional
    public SellingPrice upsert(SAPMaterialPricing sapMaterialPricing) {
        // Find existing record based on key attribute
        SellingPriceMaster existingPrice = repository.findByKeyAttributes(
                sapMaterialPricing.getSapItemNo(),
                sapMaterialPricing.getUomCode(),
                sapMaterialPricing.getStartingDate(),
                sapMaterialPricing.getEndingDate()
        ).orElse(null);

        SellingPriceMaster sellingPriceMaster;
        if(existingPrice != null){
            //update the existing selling price master
            sellingPriceMaster = existingPrice;
        } else {
            //create new selling price
            sellingPriceMaster = new SellingPriceMaster();
        }

        //map fields from sap material pricing to selling price master
        sellingPriceMaster.setSapMaterialCode(sapMaterialPricing.getSapItemNo());
        sellingPriceMaster.setNavItemNo(sapMaterialPricing.getSapItemNo());
        sellingPriceMaster.setSalesCode(sapMaterialPricing.getSalesCode());
        sellingPriceMaster.setSalesType(sapMaterialPricing.getSalesType());
        sellingPriceMaster.setUnitOfMeasure(sapMaterialPricing.getUomCode());
        sellingPriceMaster.setUnitPriceInclVat(sapMaterialPricing.getUnitPriceInclVat());
        sellingPriceMaster.setUnitPrice(sapMaterialPricing.getUnitPrice());
        sellingPriceMaster.setCurrency(sapMaterialPricing.getCurrencyCode());
        sellingPriceMaster.setVariantCode(sapMaterialPricing.getVariantCode());
        sellingPriceMaster.setStartDate(sapMaterialPricing.getStartingDate());
        sellingPriceMaster.setEndDate(sapMaterialPricing.getEndingDate());
        sellingPriceMaster.setIsActive(sapMaterialPricing.getEndingDate().isAfter(LocalDate.now()));
        sellingPriceMaster.setPricesInclVat(sapMaterialPricing.getPricesIncluVat());
        sellingPriceMaster.setAllowInvDisc(sapMaterialPricing.getAllowInvDisc());
        sellingPriceMaster.setVatBusPostGrpPric(sapMaterialPricing.getVatBusPostGrpPric());
        sellingPriceMaster.setAllowLineDisc(sapMaterialPricing.getAllowLineDisc());

        //save the updated or new selling price entity
        SellingPriceMaster savedSellingPrice = repository.save(sellingPriceMaster);

        //convert and return the DTO
        return convertToDto(savedSellingPrice);






    }
}
