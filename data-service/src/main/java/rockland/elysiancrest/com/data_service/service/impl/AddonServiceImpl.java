package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.AddonDTO;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;
import rockland.elysiancrest.com.data_service.service.AddonService;

import java.util.List;

@Service
public class AddonServiceImpl extends BaseServiceImpl<Addon, AddonDTO> implements AddonService {

    private final ModelMapper modelMapper;

    public AddonServiceImpl(JpaRepository<Addon, Long> repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    @Override
    public AddonDTO convertToDto(Addon addon) {
        return modelMapper.map(addon, AddonDTO.class);
    }

    @Override
    public Addon convertToEntity(AddonDTO addonDTO) {
        return modelMapper.map(addonDTO, Addon.class);
    }

    @Override
    public List<Addon> findAll() {
        return repository.findAll();
    }

}
