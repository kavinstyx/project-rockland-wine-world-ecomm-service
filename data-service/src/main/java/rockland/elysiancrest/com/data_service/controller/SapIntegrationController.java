package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.ExchangeRate;
import com.commonlibrary.contract.v1.Product;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.SellingPrice;
import com.commonlibrary.contract.v1.sap.SAPExchangeRate;
import com.commonlibrary.contract.v1.sap.SAPMaterialItem;
import com.commonlibrary.contract.v1.sap.SAPMaterialPricing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.service.ExchangeRateMasterService;
import rockland.elysiancrest.com.data_service.service.ProductMasterService;
import rockland.elysiancrest.com.data_service.service.SellingPriceMaserService;

@Slf4j
@RestController
@RequestMapping("api/sap-integration")
@RequiredArgsConstructor
@CrossOrigin
public class SapIntegrationController {

    private final ProductMasterService productMasterService;
    private final ExchangeRateMasterService exchangeRateMasterService;
    private final SellingPriceMaserService sellingPriceMaserService;

    @PostMapping("/material-item")
    public ResponseEntity<Response<Product>> upsertMaterialItem(@RequestBody SAPMaterialItem sapMaterialItem) {

        Product upsert = productMasterService.upsert(sapMaterialItem);

        return ResponseEntity.ok(Response.success(upsert,"MaterialItem added successfully"));

    }

    @PostMapping("/exchange-rate")
    public ResponseEntity<Response<ExchangeRate>> upsertExchangeRate(@RequestBody SAPExchangeRate exchangeRate) {

        ExchangeRate upsert = exchangeRateMasterService.upsert(exchangeRate);

        return ResponseEntity.ok(Response.success(upsert,"Exchange added successfully"));

    }

    @PostMapping("/material-selling-price")
    public ResponseEntity<Response<SellingPrice>> upsertMaterialSellingPrice(@RequestBody SAPMaterialPricing sapMaterialPricing){

        SellingPrice upsert = sellingPriceMaserService.upsert(sapMaterialPricing);

        return ResponseEntity.ok(Response.success(upsert, "Selling price added successfully"));
    }
}
