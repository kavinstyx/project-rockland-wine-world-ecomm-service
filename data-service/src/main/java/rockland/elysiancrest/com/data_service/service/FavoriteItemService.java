package rockland.elysiancrest.com.data_service.service;

import org.springframework.http.ResponseEntity;
import rockland.elysiancrest.com.data_service.dto.FavoriteItemDTO;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.entity.favourites.FavoriteItem;

import java.util.List;

public interface FavoriteItemService {
    List<FavoriteItem> getFavoritesByUser(Long userId);

    ResponseEntity<Response<FavoriteItemDTO>> addFavorite(Long userId, Long productId);

    boolean removeFavorite(Long userId, Long productId);

}

