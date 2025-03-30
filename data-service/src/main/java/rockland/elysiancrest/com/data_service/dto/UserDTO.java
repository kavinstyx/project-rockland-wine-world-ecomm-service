package rockland.elysiancrest.com.data_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private int id;
    private String name;
    private String address;
    private String username;
    private String password;
    private String contactNumbers;
    private String nic;
    private String userRole;
    private String email;
    private String token;
    private Boolean isEnabled;
//    private String country;
    private boolean isVerified;

    private String profilePicPath;
    private String profilePicTemporaryLink;

    private List<AddressDTO> billingAddresses;
    private List<AddressDTO> deliveryAddresses;
}
