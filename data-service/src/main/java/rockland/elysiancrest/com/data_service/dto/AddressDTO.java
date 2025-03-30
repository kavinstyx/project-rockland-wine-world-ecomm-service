package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

@Data
public class AddressDTO {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private String contactNumber;
    private String deliveryInstructions;

}
