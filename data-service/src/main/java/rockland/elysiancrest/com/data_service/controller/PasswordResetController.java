package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.data.PasswordResetRequest;
import rockland.elysiancrest.com.data_service.service.PasswordResetService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/pwdResetController")
@CrossOrigin
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/resetRequest")
    public ResponseEntity<Response<String>> requestPasswordReset(@RequestParam(value = "email") String email) {
        // Check if the email parameter is missing or empty
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Response.<String>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Email is required")
                            .build());
        }

        // Call the password reset service to send the reset email
        Response<String> response = passwordResetService.sendPasswordResetEmail(email.trim());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<Response<String>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {

        Response<String> response = passwordResetService.resetPassword(
                passwordResetRequest.getEmail(),
                passwordResetRequest.getOtpCode(),
                passwordResetRequest.getNewPassword()
        );

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getCode()).body(response);
        }
    }

}
