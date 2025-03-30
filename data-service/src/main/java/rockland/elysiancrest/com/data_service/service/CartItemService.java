package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;

public interface CartItemService  extends BaseService<CartItem, CartItemDTO>{
    CartItemDTO convertToDto(CartItem cartItem);
    CartItem convertToEntity(CartItemDTO cartItemDTO);
}
