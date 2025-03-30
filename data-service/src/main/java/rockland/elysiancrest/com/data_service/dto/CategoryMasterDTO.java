package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMasterDTO {
    private Long id;
    private String categoryName;
    private List<ProductMaster> products;
}
