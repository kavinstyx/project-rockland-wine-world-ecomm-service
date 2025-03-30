package rockland.elysiancrest.com.data_service.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteItemRequest {
    private Long userId;
    private Long productId;
}

