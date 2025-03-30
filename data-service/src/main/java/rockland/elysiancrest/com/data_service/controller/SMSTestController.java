package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.SmsApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/sms")
public class SMSTestController {

    private final SmsApiService smsApiService ;
    private final EmailService emailService ;

    @GetMapping("/testOtpSms")
    public ResponseEntity<Response<String>> sendTestOtp() {
        smsApiService.sendSms("94713009367","test message", "");
        return ResponseEntity.ok(Response.<String>builder().code(HttpStatus.OK.value()).status(Status.SUCCESS)
                .data("OK").build());
    }

    @GetMapping("/testOtpEmail")
    public Response<String> sendTestOtpEmail() {

        try {

            //generate otp
            String otpCode = "12345";
            String email = "manikya@capricon.lk";

            //prepare the template context
            Context context = new Context();
            context.setVariable("username", "testUsername");
            context.setVariable("resetLink", otpCode);

            //send otp email
            emailService.passwordResetEmail(
                    email,
                    "Wine World Password Reset",
                    "forget_password_email.html",
                    context);
            return Response.<String>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Password reset OTP sent successfully")
                    .data("Password reset OTP sent successfully")
                    .build();
        } catch (Exception e) {
            return Response.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(Status.ERROR)
                    .message("Failed to send password reset OTP")
                    .build();
        }

    }


}
