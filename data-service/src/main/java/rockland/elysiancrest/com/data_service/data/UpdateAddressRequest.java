package rockland.elysiancrest.com.data_service.data;

import lombok.Data;
import rockland.elysiancrest.com.data_service.dto.AddressDTO;

import java.util.List;

@Data
public class UpdateAddressRequest {
    private List<AddressDTO> billingAddresses;
    private List<AddressDTO> deliveryAddresses;
}