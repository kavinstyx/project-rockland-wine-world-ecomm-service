package rockland.elysiancrest.com.data_service.data;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String name;
    private String address;
    private String email;
    private String contactNumbers;
}
