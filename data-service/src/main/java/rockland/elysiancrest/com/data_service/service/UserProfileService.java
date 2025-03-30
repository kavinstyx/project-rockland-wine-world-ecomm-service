package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rockland.elysiancrest.com.data_service.data.ChangePasswordRequest;
import rockland.elysiancrest.com.data_service.data.UserProfileRequest;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.entity.PasswordResetToken;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.repo.PasswordResetTokenRepository;
import rockland.elysiancrest.com.data_service.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlobStorageService blobStorageService;

    public UserProfileService(UserRepo userRepository, PasswordEncoder passwordEncoder, BlobStorageService blobStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blobStorageService = blobStorageService;
    }

    public Response<User> getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Response.<User>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("User not found")
                    .build();
        }
        return Response.<User>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("User found")
                .data(user)
                .build();
    }

    public User updateUserProfile(Long userId, UserProfileRequest profileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validate if the new email already exists (excluding the current user)
        if (userRepository.findByEmail(profileRequest.getEmail()).isPresent() &&
                !userRepository.findByEmail(profileRequest.getEmail()).get().getId().equals(userId)) {
            throw new IllegalArgumentException("The email address is already taken by another user.");
        }

        // Update user details
        user.setName(profileRequest.getName());
        user.setAddress(profileRequest.getAddress());
        user.setEmail(profileRequest.getEmail());
        user.setContactNumbers(profileRequest.getContactNumbers());

        return userRepository.save(user);
    }


    public Response<String> changePassword(Long userId, ChangePasswordRequest passwordRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Response.<String>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("User not found")
                    .build();
        }

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            return Response.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(Status.ERROR)
                    .message("Old password is incorrect")
                    .build();
        }

        // Validate the new password (e.g., minimum length)
        if (passwordRequest.getNewPassword().length() < 8) {
            return Response.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(Status.ERROR)
                    .message("New password must be at least 8 characters long")
                    .build();
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);

        return Response.<String>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("Password updated successfully")
                .data("Password updated successfully")
                .build();
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);

    }

    public void save(User user) {
        userRepository.save(user);
    }


    public String generateTemporaryUrl(String blobName, int expiryTimeInMinutes)
    {
        return blobStorageService.generateTemporaryUrl(blobName, expiryTimeInMinutes);
    }
    public Response<User> uploadProfilePic(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Response.<User>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("User not found")
                    .build();
        }

        try {
            // Generate a unique file name
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // Upload to blob storage
            String profilePicUrl = blobStorageService.uploadFile(fileName, file.getInputStream(), file.getSize());

            // Save the URL in the user profile
            user.setProfilePicPath(profilePicUrl);

            // Save the updated user profile
            userRepository.save(user);

            // Generate temporary link for uploaded file
            String temporaryLink = blobStorageService.generateTemporaryUrl(fileName, 60); // URL valid for 60 mins
            user.setProfilePicTemporaryLink(temporaryLink);

            // Save the updated user profile again (to include temporary link)
            userRepository.save(user);

            return Response.<User>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Profile picture uploaded successfully")
                    .data(user)
                    .build();

        } catch (Exception e) {
            return Response.<User>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(Status.ERROR)
                    .message("Profile picture upload failed: " + e.getMessage())
                    .build();
        }
    }
}

