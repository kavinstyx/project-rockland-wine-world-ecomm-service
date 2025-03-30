package rockland.elysiancrest.com.data_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.FavoriteItemDTO;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.favourites.FavoriteItem;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.repo.FavoriteItemRepository;
import rockland.elysiancrest.com.data_service.service.FavoriteItemService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteItemServiceImpl implements FavoriteItemService {

    private final FavoriteItemRepository favoriteItemRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<FavoriteItem> getFavoritesByUser(Long userId) {
        return favoriteItemRepository.findByUserId(userId);
    }

    @Override
    public ResponseEntity<Response<FavoriteItemDTO>> addFavorite(Long userId, Long productId) {
        // Check if the item is already marked as a favorite
        Optional<FavoriteItem> existingFavorite = favoriteItemRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) {
            // If already marked as a favorite, return a response with the existing data
            FavoriteItem favoriteItem = existingFavorite.get();
            FavoriteItemDTO favoriteItemDTO = new FavoriteItemDTO();
            favoriteItemDTO.setId(favoriteItem.getId());
            favoriteItemDTO.setUserId(favoriteItem.getUser().getId());
            favoriteItemDTO.setProductId(favoriteItem.getProduct().getId());
            favoriteItemDTO.setIsFavorite(favoriteItem.getIsFavorite());

            Response<FavoriteItemDTO> response = Response.<FavoriteItemDTO>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("This product is already marked as a favorite.")
                    .data(favoriteItemDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        FavoriteItem favoriteItem = new FavoriteItem();
        User user = new User();
        user.setId(userId);
        favoriteItem.setUser(user); // Assuming the User object is referenced by ID

        ProductMaster product = new ProductMaster();
        product.setId(productId);
        favoriteItem.setProduct(product); // Assuming the ProductMaster object is referenced by ID

        favoriteItem.setIsFavorite(true);

        favoriteItemRepository.save(favoriteItem);

        // Convert the saved entity to a DTO
        FavoriteItemDTO favoriteItemDTO = new FavoriteItemDTO();
        favoriteItemDTO.setId(favoriteItem.getId());
        favoriteItemDTO.setUserId(favoriteItem.getUser().getId());
        favoriteItemDTO.setProductId(favoriteItem.getProduct().getId());
        favoriteItemDTO.setIsFavorite(favoriteItem.getIsFavorite());

        // Build the response
        Response<FavoriteItemDTO> response = Response.<FavoriteItemDTO>builder()
                .code(HttpStatus.CREATED.value())
                .status(Status.SUCCESS)
                .message("Favorite item added successfully.")
                .data(favoriteItemDTO)
                .build();

        // Return the ResponseEntity
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Override
    public boolean removeFavorite(Long userId, Long productId) {
        Optional<FavoriteItem> favoriteItem = favoriteItemRepository.findByUserIdAndProductId(userId, productId);

        if (favoriteItem.isPresent()) {
            favoriteItemRepository.delete(favoriteItem.get());
            return true;
        } else {
            return false; // Return false if the favorite item doesn't exist
        }
    }
}

