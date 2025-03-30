package rockland.elysiancrest.com.data_service.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.service.BaseService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Transactional
public abstract class BaseServiceImpl<T, D> implements BaseService<T, D> {

    protected JpaRepository<T, Long> repository;

    public BaseServiceImpl(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }


    @Override
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public T update(Long id, T entity) {
        Optional<T> existingEntity = repository.findById(id);
        if (!existingEntity.isPresent()) {
            throw new IllegalArgumentException("Entity not found");
        }    // Copy properties from the input entity to the existing entity
        T updatedEntity = existingEntity.get();
        BeanUtils.copyProperties(entity, updatedEntity, "id"); // Copy all properties except the id
        return repository.save(updatedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
