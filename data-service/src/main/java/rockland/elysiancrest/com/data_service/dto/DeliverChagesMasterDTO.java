package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverChagesMasterDTO {

    private Long id;
    private String deliveryType;
    private Double basePrice;
    private Double twoBottlesPrice;
    private Double threeBottlesPrice;
    private Double fourBottlesPrice;
    private Double fiveBottlesPrice;
    private Double sixBottlesPrice;
    private Double sevenBottlesPrice;
    private Double eightBottlesPrice;

    private Long cityId; //id for city master
}
