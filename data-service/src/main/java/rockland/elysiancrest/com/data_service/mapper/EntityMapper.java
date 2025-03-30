package rockland.elysiancrest.com.data_service.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityMapper<Entity, Dto> {

    private final ModelMapper modelMapper;

    public EntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Entity toEntity(Dto dto, Class<Entity> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    public Dto toDto(Entity entity, Class<Dto> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public void updateEntityFromDto(Dto dto, Entity entity) {
        modelMapper.map(dto, entity);
    }

    public List<Entity> toEntities(List<Dto> dtos, Class<Entity> entityClass) {
        return dtos.stream().map(dto -> toEntity(dto, entityClass)).collect(Collectors.toList());
    }

    public List<Dto> toDtos(List<Entity> entities, Class<Dto> dtoClass) {
        return entities.stream().map(entity -> toDto(entity, dtoClass)).collect(Collectors.toList());
    }

    public Set<Entity> toEntities(Set<Dto> dtos, Class<Entity> entityClass) {
        return dtos.stream().map(dto -> toEntity(dto, entityClass)).collect(Collectors.toSet());
    }

    public Set<Dto> toDtos(Set<Entity> entities, Class<Dto> dtoClass) {
        return entities.stream().map(entity -> toDto(entity, dtoClass)).collect(Collectors.toSet());
    }

    public Date mapDate(LocalTime localTime) {
        Instant instant = localTime.atDate(LocalDate.of(2000, 1, 1))
                .atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public LocalTime mapTime(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
    }

}