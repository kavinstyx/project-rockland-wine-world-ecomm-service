package rockland.elysiancrest.com.data_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.data.FavoriteItemRequest;
import rockland.elysiancrest.com.data_service.dto.FavoriteItemDTO;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.entity.favourites.FavoriteItem;
import rockland.elysiancrest.com.data_service.service.FavoriteItemService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin
public class FavoriteItemController {

    private final FavoriteItemService favoriteItemService;
    private final ModelMapper modelMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteItemDTO>> getFavorites(@PathVariable Long userId) {
        List<FavoriteItem> favorites = favoriteItemService.getFavoritesByUser(userId);

        // Correct mapping from List<FavoriteItem> to List<FavoriteItemDTO>
        List<FavoriteItemDTO> favoriteItemDTOs = favorites.stream()
                .map(favorite -> modelMapper.map(favorite, FavoriteItemDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteItemDTOs);
    }

    @PostMapping
    public ResponseEntity<Response<FavoriteItemDTO>> addFavorite(@RequestBody FavoriteItemRequest request) {
        return favoriteItemService.addFavorite(request.getUserId(), request.getProductId());
    }


    @DeleteMapping
    public ResponseEntity<Response<String>> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        boolean isRemoved = favoriteItemService.removeFavorite(userId, productId);

        if (isRemoved) {
            return ResponseEntity.ok(
                    Response.<String>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("Favorite item removed successfully.")
                            .data(null)
                            .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.<String>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Favorite item not found.")
                            .data(null)
                            .build()
            );
        }
    }

}

