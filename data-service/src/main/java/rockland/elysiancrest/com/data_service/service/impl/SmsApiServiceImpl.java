package rockland.elysiancrest.com.data_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.adeonatech.dto.SendTextBody;
import net.adeonatech.dto.SendTextResponse;
import net.adeonatech.dto.TokenBody;
import net.adeonatech.dto.TokenResponse;
import net.adeonatech.service.SendSMSImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.service.SmsApiService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SmsApiServiceImpl  implements SmsApiService {

    private TokenResponse tokenResponse;
    private LocalDate tokenDate;

    @Value("${sms.gateway.username}")
    private String smsGatewayUsername;

    @Value("${sms.gateway.password}")
    private String smsGatewayPassword;

    @Value("${sms.gateway.mask}")
    private String smsGatewayMask;

    private void getAccessToken() {

        if (smsGatewayUsername == null || smsGatewayPassword == null) {
            throw new RuntimeException("SMS Gateway Username and Password are required");
        }

        TokenBody tokenBody = new TokenBody();

        tokenBody.setUsername(smsGatewayUsername);
        tokenBody.setPassword(smsGatewayPassword);

        SendSMSImpl sendSMS = new SendSMSImpl();

        try {
            tokenResponse = sendSMS.getToken(tokenBody);
            tokenDate = LocalDate.now();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to send SMS : Access Token");
        }
    }

    public void sendSms(String contactNumber, String message, String transactionId) {

        // Check if transactionId is null or empty
        if (transactionId == null || transactionId.trim().isEmpty()) {
            long timestamp = Instant.now().toEpochMilli(); // Get current timestamp in milliseconds
            int randomFourDigit = ThreadLocalRandom.current().nextInt(1000, 10000); // Generate a random 4-digit number
            transactionId = timestamp + "" + randomFourDigit; // Combine timestamp and random number with a hyphen
        }

        SendSMSImpl sendSMS = new SendSMSImpl();
        SendTextBody sendTextBody = new SendTextBody();

        sendTextBody.setMsisdn(sendSMS.setMsisdns(new String[]{contactNumber}));
        //Set the mask
        //when not set; uses the default mask
        if(smsGatewayMask != null && !smsGatewayMask.trim().isEmpty()) {
            sendTextBody.setSourceAddress(smsGatewayMask);
        }
        sendTextBody.setMessage(message);
        sendTextBody.setTransaction_id(transactionId);

        if (tokenResponse == null || tokenDate.isBefore(LocalDate.now())) getAccessToken();

        try {
            final SendTextResponse sendTextResponse = sendSMS.sendText(sendTextBody, tokenResponse.getToken());
            if ("failed".equals(sendTextResponse.getStatus())) {
                throw new RuntimeException("Failed to send SMS : " + sendTextResponse.getComment());
            }
        } catch (Exception e) {
            log.error("--------------- Send SMS : {}", e.getMessage());
            throw new RuntimeException("Failed to send SMS : Send SMS");
        }
    }

}
