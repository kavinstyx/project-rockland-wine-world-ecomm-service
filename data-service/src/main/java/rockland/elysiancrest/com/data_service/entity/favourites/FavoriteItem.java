package rockland.elysiancrest.com.data_service.entity.favourites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

@Entity
@Table(name = "favorite_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductMaster product;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = true;
}

