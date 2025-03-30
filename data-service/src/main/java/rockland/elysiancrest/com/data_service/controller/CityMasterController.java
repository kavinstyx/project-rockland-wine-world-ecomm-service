package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.City;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.service.CityMasterService;

@Slf4j
@RestController
@RequestMapping("api/cities")
@CrossOrigin
public class CityMasterController extends AbstractCrudController<City,Long,CityMasterService>{

    protected CityMasterController(CityMasterService service) {
        super(service);
    }

    @GetMapping("/billable")
    public ResponseEntity<Response<Page<City>>> findAllBillable(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(Response.<Page<City>>builder().code(HttpStatus.OK.value())
                .status(Status.SUCCESS).data(service.findAllCities(pageRequest)).build());
    }

}
