package rockland.elysiancrest.com.data_service.service.impl;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.service.CrudService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


@Getter
public abstract class CrudServiceImpl<Entity, ID, Repository extends JpaRepository<Entity, ID>, Resource>
        implements CrudService<Resource, ID> {

    protected final Repository repository;
    protected final ModelMapper modelMapper;
    private final Class<Entity> entityClass;
    private final Class<Resource> resourceClass;


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CrudServiceImpl(Repository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] typeArgs = type.getActualTypeArguments();

        this.resourceClass = ((Class) typeArgs[3]);
        this.entityClass = ((Class) typeArgs[0]);
    }

    protected Entity convertToEntity(Resource resource) {

        return modelMapper.map(resource, entityClass);
    }

    protected Resource convertToDto(Entity entity) {

        return modelMapper.map(entity, resourceClass);
    }

    @Override
    public Resource create(Resource resource) {
        Entity entity = convertToEntity(resource);
        Entity savedEntity = repository.save(entity);
        return convertToDto(savedEntity);
    }

    @Override
    public Resource update(ID id, Resource resource) {
        Entity existingEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requested resource with id: " + id + " does not exist."));

        // Map the incoming DTO onto the existing entity for patching
        modelMapper.map(resource, existingEntity);

        Entity updatedEntity = repository.save(existingEntity);
        return convertToDto(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource findById(ID id) {
        Entity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requested resource with id: " + id + " does not exist."));

        return convertToDto(entity);
    }

    @Override
    public Page<Resource> findAll(PageRequest pageRequest) {
        Page<Entity> entityPage = repository.findAll(pageRequest);

        return entityPage.map(this::convertToDto);
    }

    @Override
    public void delete(Resource resource) {
        Entity entity = convertToEntity(resource);
        repository.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Requested resource with id: " + id + " does not exist.");
        }
        repository.deleteById(id);
    }
}




