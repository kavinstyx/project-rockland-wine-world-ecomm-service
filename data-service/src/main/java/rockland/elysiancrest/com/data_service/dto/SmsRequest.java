package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

@Data
public class SmsRequest {
    private String phoneNumber;
    private String message;
    private String smsApiUsername;
    private String smsApiPassword;
    private String smsApiMask;

    public SmsRequest(String phoneNumber, String message, String smsApiUsername, String smsApiPassword, String smsApiMask) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.smsApiUsername = smsApiUsername;
        this.smsApiPassword = smsApiPassword;
        this.smsApiMask = smsApiMask;
    }
}