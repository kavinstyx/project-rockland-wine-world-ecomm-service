package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.ExchangeRate;
import com.commonlibrary.contract.v1.sap.SAPExchangeRate;


public interface ExchangeRateMasterService extends CrudService<ExchangeRate, Long>{

    ExchangeRate upsert(SAPExchangeRate sapExchangeRate);
}
