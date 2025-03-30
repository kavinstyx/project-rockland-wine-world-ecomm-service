package rockland.elysiancrest.com.data_service.service;

public interface SmsApiService {
    void sendSms(String contactNumber, String message, String transactionId);
}
