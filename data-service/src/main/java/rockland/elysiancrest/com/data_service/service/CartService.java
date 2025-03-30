package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import rockland.elysiancrest.com.data_service.dto.CartAddonDTO;
import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartSubmitRequest;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.util.Optional;

public interface CartService extends BaseService<CartMaster, CartDTO> {
    CartDTO convertToDto(CartMaster cartMaster);

    CartMaster convertToEntity(CartDTO cartDTO);

    Page<CartMaster> findByUserIdAndCartType(Long userId, String cartType, Pageable pageable);

    Optional<CartMaster> findByUserIdAndCartId(Long userId, Long cartId);

    CartMaster updateCartTotal(CartMaster cartMaster);

    OrderMaster convertCartToOrder(CartMaster cart);

    String generateSessionId();

    ResponseEntity<Response<String>> deleteCart(Long userId, String sessionId, String cartType);

    int getCartItemCount(Long userId, String cartType);

    Optional<CartMaster> findBySessionId(String sessionId);

    CartMaster getCart(Long userId, String cartType);

    ResponseEntity<Response<CartDTO>> addCartItem(
            Long userId,
            String sessionId,
            String cartType,
            Long cityId,
            CartItemDTO cartItemDTO);

    ResponseEntity<Response<CartAddonDTO>> submitAddons(
            Long cartId,
            CartSubmitRequest cartSubmitRequest);

    ResponseEntity<Response<CartDTO>> updateSpecialInstructions(Long cartId, String specialInstructions);
}