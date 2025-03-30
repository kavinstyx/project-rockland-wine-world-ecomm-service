package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.authFilter.JwtUtil;
import rockland.elysiancrest.com.data_service.data.LoginRequest;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.otp.Otp;
import rockland.elysiancrest.com.data_service.repo.OtpRepository;
import rockland.elysiancrest.com.data_service.repo.UserRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.UserService;
import rockland.elysiancrest.com.data_service.util.FreeTextSearchSpecifications;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserDTO> implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    @Value("${app.domain}")
    private String appDomain;

    public UserServiceImpl(UserRepo repository, ModelMapper modelMapper, UserRepo userRepository, PasswordEncoder passwordEncoder, EmailService emailService, OtpRepository otpRepository) {
        super(repository);
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    @Override
    public UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    @Transactional
    public Response<UserDTO> registerUser(UserDTO userDTO) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                return Response.<UserDTO>builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .status(Status.ERROR)
                        .message("Email already exists!")
                        .build();
            }

            if (userRepository.findByContactNumbers(userDTO.getContactNumbers().trim()).isPresent()) {
                return Response.<UserDTO>builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .status(Status.ERROR)
                        .message("Contact Number already exists!")
                        .build();
            }

            // Map UserDTO to User entity
            User user = new User();
            user.setName(userDTO.getName());
            user.setAddress(userDTO.getAddress());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setContactNumbers(userDTO.getContactNumbers().trim());
            user.setUserRole("USER");

            userRepository.save(user);
            UserDTO userDTO1 = convertToDto(user);
            userDTO1.setPassword(null);

            return Response.<UserDTO>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Registration Successful!")
                    .data(userDTO1)
                    .build();

        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            return Response.<UserDTO>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(Status.ERROR)
                    .message("Registration failed!")
                    .build();
        }
    }


    @Override
    public UserDTO loginUser(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Check if the password is correct
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                // Generate a new JWT token for the user
                JwtUtil jwtUtil = new JwtUtil();
                String jwtToken = jwtUtil.generateToken(user.getEmail());

                // Optionally update the token field in the user entity (if stored persistently)
                user.setToken(jwtToken);
                userRepository.save(user);

                // Convert to UserDTO and nullify sensitive data like the password
                UserDTO userDTO = convertToDto(user);
                userDTO.setPassword(null); // Ensure password is not exposed

                // Set the token in the DTO
                userDTO.setToken(jwtToken);

                return userDTO;
            }
        }

        return null;
    }

    @Override
    public Response<UserDTO> markUserAsVerified(String contact, String email) {
        Optional<Otp> otp = otpRepository.findTopByContactAndEmailOrderByCreatedAtDesc(contact, email);

        if (otp.isPresent()) {
            User user = otp.get().getUser();

            if (user != null) {
                user.setVerified(true);

                this.save(user);

                UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                userDTO.setPassword(null);


                String account_link = appDomain + "/my-account/";

                Context context = new Context();
                context.setVariable("name", user.getName());
                context.setVariable("accountLink", account_link);
                context.setVariable("userEmail", user.getEmail());

                try {
                    emailService.registerEmail(
                            user.getEmail(),
                            "Welcome to Wine World!",
                            "welcome_email.html",
                            context
                    );
                } catch (MessagingException e) {
                    throw new RuntimeException("Failed to send welcome email");
                }

                return Response.<UserDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("User verified successfully.")
                        .data(userDTO)
                        .build();
            } else {
                return Response.<UserDTO>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(Status.ERROR)
                        .message("No user associated with the provided contact or email.")
                        .build();
            }
        } else {
            return Response.<UserDTO>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("OTP not found for the provided contact or email.")
                    .build();
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);

    }

    //get all with search
    @Override
    public Page<UserDTO> getUsersWithPagination(int page, int size, String searchString) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<User> userSpecification = FreeTextSearchSpecifications.<User>builder()
                .withSearchString(searchString)
                .addEntityField("name", String.class)
                .addEntityField("username", String.class)
                .build();

        Page<User> userPage = userRepository.findAll(userSpecification, pageable);

        return userPage.map(user -> {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            userDTO.setPassword(null); // Set password to null
            return userDTO;
        });
    }

    @Override
    public Response<Void> toggleUserActivation(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return Response.<Void>builder()
                   .code(HttpStatus.NOT_FOUND.value())
                   .status(Status.ERROR)
                   .message("User not found.")
                   .build();
        }

        User user = userOptional.get();
        user.setIsEnabled(!user.getIsEnabled()); // Automatically toggle the current state
        userRepository.save(user);

        return Response.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message(user.getIsEnabled() ? "User account activated successfully." : "User account deactivated successfully.")
                .build();
    }

    @Override
    public UserDTO getUserById(Long userId) {
        try{
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDTO userDTO = convertToDto(user);

            return Response.success(userDTO, "User fetched successfully").getData();
        } catch (Exception e){
            throw new RuntimeException("Error occurred while fetching user");
        }
    }

    @Override
    @Transactional
    public Response<UserDTO> updateUser(Long userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!userDTO.getEmail().equals(existingUser.getEmail())){
            if(userRepository.findByEmail(userDTO.getEmail()).isPresent()){
                throw new RuntimeException("Email already exists");
            }
        }

        try{
            existingUser.setName(userDTO.getName());
            existingUser.setAddress(userDTO.getAddress());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setContactNumbers(userDTO.getContactNumbers());
            existingUser.setUserRole(userDTO.getUserRole());

            userRepository.save(existingUser);

            UserDTO updatedUserDTO = convertToDto(existingUser);
            updatedUserDTO.setPassword(null); // Ensure password is not exposed in the response

            return Response.<UserDTO>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("User updated successfully.")
                    .data(updatedUserDTO)
                    .build();
        } catch (Exception e){

            throw new RuntimeException("Error occured while update user"+ e.getMessage());
        }
    }


}
