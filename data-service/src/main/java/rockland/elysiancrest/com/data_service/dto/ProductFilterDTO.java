package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {
    private String sizeCategory; // Values: "small", "medium", "large", "extra_large"
    private List<String> brands;
    private String sortBy; // Values: "price_low_high", "price_high_low", "fast_moving"
    private boolean ascending;
}
