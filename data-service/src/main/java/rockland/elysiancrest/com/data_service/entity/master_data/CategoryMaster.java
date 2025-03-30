package rockland.elysiancrest.com.data_service.entity.master_data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.util.List;

@Entity
@Table(name = "category_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class CategoryMaster extends BaseEntity {

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<ProductMaster> products;
}
