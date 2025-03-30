package rockland.elysiancrest.com.data_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rockland.elysiancrest.com.data_service.dto.config.ShopOpenHoursDTO;
import rockland.elysiancrest.com.data_service.service.ShopOpenHoursService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/shop_opening_hours")
public class ShopOpeningHoursController extends AbstractCrudController<ShopOpenHoursDTO, Long, ShopOpenHoursService> {

    protected ShopOpeningHoursController(ShopOpenHoursService service) {
        super(service);
    }
}
