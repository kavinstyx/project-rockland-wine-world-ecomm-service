package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentCategoryProductDTO {
    private String str;
    private String srch;
    private Long dep;
    private Long cat;
    private Long prod;
    private String imageLink;


}