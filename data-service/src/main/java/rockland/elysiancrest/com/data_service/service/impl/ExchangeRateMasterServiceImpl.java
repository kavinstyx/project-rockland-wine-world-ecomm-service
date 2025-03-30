package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.ExchangeRate;
import com.commonlibrary.contract.v1.sap.SAPExchangeRate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.entity.master_data.ExchangeRateMaster;
import rockland.elysiancrest.com.data_service.repo.ExchangeRateMasterRepo;
import rockland.elysiancrest.com.data_service.service.ExchangeRateMasterService;

@Service
@Transactional
public class ExchangeRateMasterServiceImpl extends CrudServiceImpl<ExchangeRateMaster,Long, ExchangeRateMasterRepo, ExchangeRate> implements ExchangeRateMasterService {

    public ExchangeRateMasterServiceImpl(ExchangeRateMasterRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);

    }

    @Override
    public ExchangeRate upsert(SAPExchangeRate sapExchangeRate) {
        // Map SAPExchangeRate to ExchangeRateMaster
        ExchangeRateMaster exchangeRateMaster = new ExchangeRateMaster();
        exchangeRateMaster.setExchangeRateType(sapExchangeRate.getKurst());
        exchangeRateMaster.setFromCurrency(sapExchangeRate.getFcurr());
        exchangeRateMaster.setToCurrency(sapExchangeRate.getTcurr());
        exchangeRateMaster.setStartDate(sapExchangeRate.getGdatu());
        exchangeRateMaster.setExRate(sapExchangeRate.getUkurs());

        // Check if a matching record already exists
        ExchangeRateMaster existingEntity = repository.findByKeyAttributes(
                sapExchangeRate.getKurst(),
                sapExchangeRate.getFcurr(),
                sapExchangeRate.getTcurr(),
                sapExchangeRate.getGdatu()
        ).orElse(null);

        if (existingEntity != null) {
            // Update the existing record
            existingEntity.setExRate(sapExchangeRate.getUkurs());
            exchangeRateMaster = repository.save(existingEntity);
        } else {
            // Create a new record
            exchangeRateMaster = repository.save(exchangeRateMaster);
        }

        // Map the saved entity to the DTO and return
        return convertToDto(exchangeRateMaster);
    }

}
