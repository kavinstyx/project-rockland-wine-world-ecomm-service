package rockland.elysiancrest.com.data_service.controller;


import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import rockland.elysiancrest.com.data_service.service.CrudService;

@Getter
public abstract class AbstractCrudController<Resource, ID, Service extends CrudService<Resource, ID>>
        extends AbstractController {

    protected final Service service;

    protected AbstractCrudController(Service service) {
        this.service = service;
    }

    @PostMapping(value = "")
    public ResponseEntity<Response<Resource>> create(@RequestBody Resource resource) {
        return ResponseEntity.ok(Response.<Resource>builder().code(HttpStatus.OK.value())
                .status(Status.SUCCESS).data(service.create(resource)).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Resource>> findResource(@PathVariable ID id) {
        return ResponseEntity.ok(Response.<Resource>builder().code(HttpStatus.OK.value()).status(Status.SUCCESS)
                .data(service.findById(id)).build());
    }

    @GetMapping()
    public ResponseEntity<Response<Page<Resource>>> findAllResource(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(Response.<Page<Resource>>builder().code(HttpStatus.OK.value())
                .status(Status.SUCCESS).data(service.findAll(pageRequest)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Resource>> updateResource(@PathVariable ID id, @RequestBody Resource resource) {
        return ResponseEntity.ok(Response.<Resource>builder().code(HttpStatus.OK.value())
                .status(Status.SUCCESS).data(service.update(id, resource)).build());
    }

    @DeleteMapping
    public ResponseEntity<Response<Resource>> deleteResource(@RequestBody Resource resource) {
        service.delete(resource);
        return ResponseEntity.ok(Response.<Resource>builder().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Resource>> deleteResourceById(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(Response.<Resource>builder().build());
    }

}
