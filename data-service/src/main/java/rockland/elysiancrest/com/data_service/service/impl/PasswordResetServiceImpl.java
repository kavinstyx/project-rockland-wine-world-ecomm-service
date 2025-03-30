package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.PasswordResetTokenDTO;
import rockland.elysiancrest.com.data_service.entity.PasswordResetToken;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.repo.UserRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.OtpService;
import rockland.elysiancrest.com.data_service.service.PasswordResetService;

import java.util.Optional;

@Service
public class PasswordResetServiceImpl extends BaseServiceImpl<PasswordResetToken, PasswordResetTokenDTO> implements PasswordResetService {

    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;

    @Value("${app.domain}")
    private String appDomain;

    public PasswordResetServiceImpl(JpaRepository<PasswordResetToken, Long> repository, ModelMapper modelMapper,
                                    UserRepo userRepo, PasswordEncoder passwordEncoder, EmailService emailService, OtpService otpService) {
        super(repository);
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @Override
    public PasswordResetTokenDTO convertToDto(PasswordResetToken passwordResetToken) {
        return modelMapper.map(passwordResetToken, PasswordResetTokenDTO.class);
    }

    @Override
    public PasswordResetToken convertToEntity(PasswordResetTokenDTO passwordResetTokenDTO) {
        return modelMapper.map(passwordResetTokenDTO, PasswordResetToken.class);
    }

    @Override
    @Transactional
    public Response<String> resetPassword(String email, String otpCode, String newPassword) {

        // Find the user by email
        Optional<User> userOptional = userRepo.findByEmail(email.trim());
        if (userOptional.isEmpty()) {
            return Response.<String>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("Unknown Error")
                    .build();
        }

        User user = userOptional.get();

        // Check if the new password is the same as the old password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return Response.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(Status.ERROR)
                    .message("New password cannot be the same as the current password.")
                    .build();
        }

        // Verify the OTP
        boolean isOtpValid = otpService.verifyOtp(user.getContactNumbers(), email.trim(), otpCode);
        if (!isOtpValid) {
            return Response.<String>builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(Status.ERROR)
                    .message("Invalid or expired OTP. Please request a new one.")
                    .build();
        }

        // Update the user's password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // Save the updated user
        userRepo.save(user);

        return Response.<String>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("Password reset successfully.")
                .data("Password reset successfully.")
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> sendPasswordResetEmail(String email) {

        Optional<User> user = userRepo.findByEmail(email.trim());
        if (user.isEmpty()) {
            return Response.<String>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("OTP resent successfully.")
                    .build();
        }

        // Generate token for password reset
//        String token = UUID.randomUUID().toString();
//        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);  // Token expires in 1 hour
//
//        // Save token to database
//        PasswordResetToken resetToken = new PasswordResetToken();
//        resetToken.setToken(token);
//        resetToken.setUser(user.get());
//        resetToken.setExpiryDate(expiryDate);
//        passwordResetTokenRepository.save(resetToken);
//
//        // Generate the reset link
//        String resetLink = appDomain + "/reset-password?token=" + token;



        // Try to send email, and return response
        try {

            //generate otp
            String otpCode = otpService.generateOtp(user.get().getContactNumbers(), email.trim(), user.get().getId().intValue());

            //prepare the template context
            Context context = new Context();
            context.setVariable("username", user.get().getUsername());
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
