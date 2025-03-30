package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantMasterDTO {

    private Long id;
    private String plantCode;
    private String name;
    private String address;
    private String contactNumber;
    private boolean webSalesEnabled;
    private String licenseNumber;
    private String licenseExpiryStatus;
    private String licenseOwner;

    private Long cityId;

}
