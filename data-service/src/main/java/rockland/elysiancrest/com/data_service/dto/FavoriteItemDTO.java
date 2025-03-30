package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FavoriteItemDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private String correctName;
    private String productImageUrl;
    private BigDecimal regularPriceInRupee;
    private BigDecimal regularPriceInDollar;
    private Boolean isFavorite;

}
