package rockland.elysiancrest.com.data_service.data;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String otpCode;
    private String newPassword;
}
