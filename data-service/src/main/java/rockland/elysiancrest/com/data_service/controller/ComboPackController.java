package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.ComboPackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rockland.elysiancrest.com.data_service.service.ComboPackService;


@Slf4j
@RestController
@RequestMapping("api/combopack")
@CrossOrigin
public class ComboPackController extends AbstractCrudController<ComboPackDto, Long, ComboPackService>{
    protected ComboPackController(ComboPackService service) {
        super(service);
    }
}
