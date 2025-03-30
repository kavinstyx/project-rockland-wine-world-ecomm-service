package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.InventoryFreezeDTO;
import rockland.elysiancrest.com.data_service.entity.cart.InventoryFreezeMaster;
import rockland.elysiancrest.com.data_service.service.InventoryFreezeService;

@Service
@Transactional
public class InventoryFreezeServiceImpl extends BaseServiceImpl<InventoryFreezeMaster, InventoryFreezeDTO> implements InventoryFreezeService {

    private final ModelMapper modelMapper;

    public InventoryFreezeServiceImpl(JpaRepository<InventoryFreezeMaster, Long> repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    @Override
    public InventoryFreezeDTO convertToDto(InventoryFreezeMaster inventoryFreezeMaster) {
        return modelMapper.map(inventoryFreezeMaster, InventoryFreezeDTO.class);
    }

    @Override
    public InventoryFreezeMaster convertToEntity(InventoryFreezeDTO freezeDTO) {
        return modelMapper.map(freezeDTO, InventoryFreezeMaster.class);
    }
}
