package rockland.elysiancrest.com.data_service.data;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

