package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseService<T, D> {
    T save(T entity);
    Optional<T> findById(Long id);
    Page<T> findAll(Pageable pageable);
    T update(Long id, T entity);
    void deleteById(Long id);
}
