package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.service.CartItemService;

@Service
public class CartItemServiceImpl extends BaseServiceImpl<CartItem, CartItemDTO> implements CartItemService {

    private final ModelMapper modelMapper;

    public CartItemServiceImpl(JpaRepository<CartItem, Long> repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;

//        configureMapper();
    }

    private void configureMapper() {
        modelMapper.addMappings(new PropertyMap<CartItem, CartItemDTO>() {
            @Override
            protected void configure() {

                map().setProductName(source.getProduct().getCorrectName());
            }
        });
    }

    @Override
    public CartItemDTO convertToDto(CartItem cartItem) {
        return modelMapper.map(cartItem, CartItemDTO.class);
    }

    @Override
    public CartItem convertToEntity(CartItemDTO cartItemDTO) {
        return modelMapper.map(cartItemDTO, CartItem.class);
    }
}