package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CrudService<Resource, ID> {

    Resource create(Resource resource);

    Resource update(ID id, Resource resource);

    Resource findById(ID id);

    Page<Resource> findAll(PageRequest pageRequest);

    void delete(Resource resource);

    void deleteById(ID id);
}