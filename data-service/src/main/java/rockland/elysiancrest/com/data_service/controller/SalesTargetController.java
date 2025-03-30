package rockland.elysiancrest.com.data_service.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.dashboard.SalesTargetDTO;
import rockland.elysiancrest.com.data_service.service.SalesTargetService;

@Slf4j
@RestController
@RequestMapping("api/sals-target")
@CrossOrigin
public class SalesTargetController extends AbstractCrudController<SalesTargetDTO, Long, SalesTargetService>{


    protected SalesTargetController(SalesTargetService service) {
        super(service);
    }
}

