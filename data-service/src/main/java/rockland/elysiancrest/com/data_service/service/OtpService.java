package rockland.elysiancrest.com.data_service.service;

import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.otp.Otp;
import rockland.elysiancrest.com.data_service.repo.OtpRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserService userService;

    public OtpService(OtpRepository otpRepository, UserService userService) {
        this.otpRepository = otpRepository;
        this.userService = userService;
    }

    public String generateOtp(String contact, String email, int userId) {
        // Check the number of OTPs generated for the given contact or email
//        int otpGenerationCount = otpRepository.countByContactAndEmail(contact, email);

//        if (otpGenerationCount >= 5) {
//            throw new IllegalStateException("OTP generation limit reached for this contact/email.");
//        }

        // Check if an OTP already exists and delete it if not verified or expired
        List<Otp> existingOtps = otpRepository.findAllByContactAndEmail(contact, email);
        otpRepository.deleteAll(existingOtps);

        // Generate a 5-digit numeric OTP
        String otpCode = String.valueOf((int) (Math.random() * 90000) + 10000);

        // Set expiration to 5 minutes from now
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(2);

        // Create a new OTP object
        Otp otp = new Otp();
        otp.setContact(contact);
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(expiresAt);

        // Find and set the User object if userId is valid
        if (userId > 0) {
            User user = userService.findById((long) userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            otp.setUser(user);
        }

//        otp.setGenerationCount(otpGenerationCount + 1); // Increment generation count
        otpRepository.save(otp); // Save the new OTP

        return otpCode; // Return OTP for sending via email/SMS
    }


    public boolean verifyOtp(String contact, String email, String otpCode) {
        Otp otp = otpRepository.findTopByContactAndEmailOrderByCreatedAtDesc(contact, email).orElse(null);

        System.out.println("client otp: " + otpCode + "   database otp :" + otp.getOtpCode());
        // Validate OTP existence and code match
        if (otp == null || !Objects.equals(otp.getOtpCode(), otpCode)) {
            return false;
        }

        // Extend expiry margin by 2 minutes
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpExpiry = otp.getExpiresAt();

        System.out.println("Current time: " + currentTime + " otp expiry: " + otpExpiry);
        if (!otp.getIsVerified() && currentTime.isBefore(otpExpiry)) {
            otp.setIsVerified(true); // Mark OTP as verified
            otpRepository.save(otp);
            return true;
        }

        return false;
    }



    public String resendOtp(String contact, String email, long userId) {
        // Check if an OTP exists for the contact/email
        Optional<Otp> existingOtp = otpRepository.findTopByContactAndEmailOrderByCreatedAtDesc(contact, email);

        if (existingOtp.isPresent()) {
            Otp otp = existingOtp.get();

            // Check if the existing OTP is still valid
            if (!otp.getIsVerified() && otp.getExpiresAt().isAfter(LocalDateTime.now())) {
                // Resend the existing OTP
                return otp.getOtpCode(); // Return the existing OTP for resending
            } else {
                // If expired, delete the existing OTP
                otpRepository.delete(otp);
            }
        }

        // Generate a new OTP
        String newOtpCode = String.valueOf((int) (Math.random() * 90000) + 10000);

        // Set expiration to 5 minutes from now
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // Create a new OTP object
        Otp newOtp = new Otp();
        newOtp.setContact(contact);
        newOtp.setEmail(email);
        newOtp.setOtpCode(newOtpCode);
        newOtp.setExpiresAt(expiresAt);

        // Find and set the User object if userId is valid
        if (userId > 0) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            newOtp.setUser(user);
        }

        otpRepository.save(newOtp);

        return newOtpCode; // Return the new OTP for resending
    }

}

