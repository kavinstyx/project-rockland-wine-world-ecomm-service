package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.ExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.service.ExchangeRateMasterService;

@Slf4j
@RestController
@RequestMapping("api/exchangeRate")
@CrossOrigin
public class ExchangeRateMasterController extends AbstractCrudController<ExchangeRate, Long, ExchangeRateMasterService>{
    protected ExchangeRateMasterController(ExchangeRateMasterService service){
        super(service);
    }
}

