package rockland.elysiancrest.com.data_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeEmailDTO {

    private Long id;
    private String email;
}
