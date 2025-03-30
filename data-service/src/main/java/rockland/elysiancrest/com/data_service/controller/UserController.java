package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.authFilter.JwtUtil;
import rockland.elysiancrest.com.data_service.data.LoginRequest;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.repo.UserRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.OtpService;
import rockland.elysiancrest.com.data_service.service.SmsApiService;
import rockland.elysiancrest.com.data_service.service.UserService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final SmsApiService smsApiService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, OtpService otpService, EmailService emailService, UserRepo userRepo,
                          SmsApiService smsApiService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.smsApiService = smsApiService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Response<Void>> register(@RequestBody UserDTO userDTO) {
        try {
            // Register the user (saves unverified user)
            Response<UserDTO> registeredUser = userService.registerUser(userDTO);

            if (registeredUser.getData() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Response.<Void>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .status(Status.ERROR)
                                .message(registeredUser.getMessage())
                                .build());
            }

            UserDTO registeredUser1 = registeredUser.getData();

            // Generate OTP and send it to the appropriate recipient
            String otp = otpService.generateOtp(
                    registeredUser1.getContactNumbers(),
                    registeredUser1.getEmail(),
                    registeredUser1.getId()
            );

            Context context = new Context();
            context.setVariable("otp", otp);

            CompletableFuture.runAsync(() -> {
                try {

                    emailService.OTPEmail(
                            registeredUser1.getEmail(),
                            "Wine World Verification OTP",
                            "OTP_email.html",
                            context
                    );

                } catch (Exception e) {

                    // Log errors to avoid interrupting the main flow
                    log.error("Error sending OTP: " + e.getMessage(), e);
                }

                if (registeredUser1.getContactNumbers() != null) {
                    String cleanedNumber = registeredUser1.getContactNumbers().replaceAll("[^\\d]", "");

                    if (cleanedNumber.startsWith("94")) {
                        smsApiService.sendSms(
                                cleanedNumber,
                                "Your Wine World OTP is: " + otp,
                                "");
                    }
                }
            });

            return ResponseEntity.ok().body(
                    Response.<Void>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("Registration successful! OTP sent.")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<Void>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message("An error occurred during registration: " + e.getMessage())
                            .build());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Response<UserDTO>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user and generate token
            UserDTO userDTO = userService.loginUser(loginRequest);

            if (userDTO != null) {
                return ResponseEntity.ok(
                        Response.<UserDTO>builder()
                                .code(HttpStatus.OK.value())
                                .status(Status.SUCCESS)
                                .message("Login successful.")
                                .data(userDTO)
                                .build()
                );
            }
        } catch (AuthenticationException e) {
            // Log exception for debugging (optional)
            e.printStackTrace();
        }

        // Handle invalid credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.<UserDTO>builder()
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .status(Status.ERROR)
                        .message("Invalid email or password.")
                        .build()
                );
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<Response<UserDTO>> verifyOtp(
            @RequestParam(name = "contact", required = false) String contact,
            @RequestParam String email,
            @RequestParam String otp) {

        // Log the incoming parameters for debugging
        System.out.println("Verifying OTP for contact: " + contact + ", email: " + email + ", otp: " + otp);

        // Validate email format (basic validation)
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Email is required and cannot be empty.")
                            .build());
        }

        // Validate OTP format (basic check to ensure OTP isn't empty)
        if (otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("OTP is required and cannot be empty.")
                            .build());
        }

        // Find user by email
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("User with the provided email does not exist.")
                            .build());
        }

        // Verify the OTP
        boolean isOtpValid = otpService.verifyOtp(user.getContactNumbers(), email.trim(), otp);

        if (isOtpValid) {
            // If OTP is valid, mark the user as verified
            Response<UserDTO> response = userService.markUserAsVerified(user.getContactNumbers().trim(), email.trim());

            if (response.getStatus() == Status.SUCCESS) {
                // Generate JWT token after successful verification
                String token = jwtUtil.generateToken(user.getEmail());

                user.setToken(token);
                userService.save(user);

                // Create UserDTO with token included
                UserDTO userDTO = response.getData();
                userDTO.setToken(token);

                return ResponseEntity.ok(Response.<UserDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("OTP verified and login successful.")
                        .data(userDTO)
                        .build());
            } else {
                // Return failure response if something goes wrong while verifying the user
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }
        } else {
            // Handle invalid or expired OTP
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Invalid or expired OTP.")
                            .build());
        }
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<Response<Void>> resendOtp(@RequestParam(name = "contact", required = false) String contact,
                                                    @RequestParam String email) {
        try {

            Optional<User> userOpt = userRepo.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<Void>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .status(Status.ERROR)
                                .message("OTP resent successfully.")
                                .build());

            }

            String otpCode = otpService.resendOtp(userOpt.get().getContactNumbers(), email.trim(), userOpt.get().getId());

//            if (otpCode.length() > 5) {
//                return ResponseEntity.badRequest().body(Response.<Void>builder()
//                        .code(HttpStatus.BAD_REQUEST.value())
//                        .status(Status.ERROR)
//                        .message("Daily OTP generation limit reached for this contact/email.")
//                        .build());
//            }

            Context context = new Context();
            context.setVariable("otp", otpCode);

            try {
                emailService.OTPEmail(
                        userOpt.get().getEmail(),
                        "Wine World Verification OTP",
                        "OTP_email.html",
                        context

                );
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send otp verification email", e);
            }

            if (userOpt.get().getContactNumbers() != null) {
                String cleanedNumber = userOpt.get().getContactNumbers().replaceAll("[^\\d]", "");

                if (cleanedNumber.startsWith("94")) {
                    smsApiService.sendSms(
                            cleanedNumber,
                            "Your Wine World OTP is: " + otpCode,
                            "");
                }
            }

            return ResponseEntity.ok().body(Response.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("OTP resent successfully.")
                    .build());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<Void>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response<Page<UserDTO>>> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size,
                                                            @RequestParam(value = "search", defaultValue = "") String searchString) {
        // Retrieve the paginated users
        Page<UserDTO> usersPage = userService.getUsersWithPagination(page, size, searchString);

        // Construct and return the response with paginated users
        Response<Page<UserDTO>> response = Response.<Page<UserDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("Users retrieved successfully.")
                .data(usersPage) // Include the usersPage in the response data
                .build();

        return ResponseEntity.ok(response); // Return the response
    }


    @ExceptionHandler({Exception.class})
    public String databaseError(Exception e) {
        e.printStackTrace(); // Log the exception
        return "UserController Error"; // Return a logical view name of an error page
    }

    @PatchMapping("/{userId}/toggle-status")
    public ResponseEntity<Response<Void>> toggleUserActivation(@PathVariable("userId") Long userId) {
        Response<Void> response = userService.toggleUserActivation(userId);
        return new ResponseEntity<>(
                response,
                HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDTO>>  getUserById(@PathVariable("id") Long userId) {
        return  ResponseEntity.ok(Response.success(userService.getUserById(userId), "User retriveved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        Response<UserDTO> response = userService.updateUser(id, userDTO);
        return ResponseEntity.ok().body(
                Response.<UserDTO>builder()
                        .data(response.getData())
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("User updated successfully!")
                        .build()
        );
    }

    //create user from backend
    @PostMapping("/create-user")
    @Transactional
    public ResponseEntity<Response<UserDTO>> registerBackendUser(@RequestBody UserDTO userDTO) {
        try {
            // Register the user
            Response<UserDTO> response = userService.registerUser(userDTO);

            return ResponseEntity.ok().body(
                    Response.<UserDTO>builder()
                            .data(response.getData())
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("User created successfully!")
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message("An error occurred during creating user: " + e.getMessage())
                            .build());
        }
    }
}

