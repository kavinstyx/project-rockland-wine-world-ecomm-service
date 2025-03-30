package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rockland.elysiancrest.com.data_service.data.ChangePasswordRequest;
import rockland.elysiancrest.com.data_service.data.UpdateAddressRequest;
import rockland.elysiancrest.com.data_service.data.UserProfileRequest;
import rockland.elysiancrest.com.data_service.dto.AddressDTO;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.entity.Address;
import rockland.elysiancrest.com.data_service.entity.AddressType;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.service.UserProfileService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final ModelMapper modelMapper;

    public UserProfileController(UserProfileService userProfileService, ModelMapper modelMapper) {
        this.userProfileService = userProfileService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> getUserProfile(@PathVariable Long userId) {
        // Fetch the user profile
        User user = userProfileService.getUserProfile(userId).getData();

        if (user == null) {
            // If the user is not found, return a 404 response with an appropriate error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("User not found for ID: " + userId)
                            .build());
        }

        UserDTO result = modelMapper.map(user, UserDTO.class);

        result.setBillingAddresses(
                user.getBillingAddresses().stream()
                        .filter(address -> "BILLING".equalsIgnoreCase(String.valueOf(address.getAddressType()))) // Filter only billing addresses
                        .map(address -> modelMapper.map(address, AddressDTO.class))
                        .collect(Collectors.toList())
        );

        result.setDeliveryAddresses(
                user.getDeliveryAddresses().stream()
                        .filter(address -> "DELIVERY".equalsIgnoreCase(String.valueOf(address.getAddressType()))) // Filter only delivery addresses
                        .map(address -> modelMapper.map(address, AddressDTO.class))
                        .collect(Collectors.toList())
        );

        // Explicitly verify each ComplaintDto includes the temporary link

        // Generate temporary link for profile picture if present
        if (user.getProfilePicPath() != null) {
            // Extract the blob name from the profile picture path
            String blobName = user.getProfilePicPath().substring(user.getProfilePicPath().lastIndexOf("/") + 1);

            // Generate a temporary URL for the profile picture (valid for 1 hour)
            String temporaryLink = userProfileService.generateTemporaryUrl(blobName, 1440); // URL valid for 1 hour
            result.setProfilePicTemporaryLink(temporaryLink);
        }


        // Return the UserDTO in a successful response
        return ResponseEntity.ok(
                Response.<UserDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("User retrieved successfully")
                        .data(result)
                        .build()
        );
    }



    @PutMapping("/{userId}")
    public ResponseEntity<Response<UserDTO>> updateUserProfile(@PathVariable("userId") Long userId,
                                                            @RequestBody UserProfileRequest profileRequest) {
        try {
            User updatedUser = userProfileService.updateUserProfile(userId, profileRequest);
            UserDTO result = modelMapper.map(updatedUser, UserDTO.class);

            return ResponseEntity.ok(Response.<UserDTO>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Profile updated successfully")
                    .data(result)
                    .build());

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message(ex.getMessage())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message("An error occurred while updating the profile")
                            .data(null)
                            .build());
        }
    }


    @PutMapping("/{userId}/change-password")
    public ResponseEntity<Response<String>> changePassword(
            @PathVariable Long userId, @RequestBody ChangePasswordRequest passwordRequest) {

        Response<String> response = userProfileService.changePassword(userId, passwordRequest);

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getCode()).body(response);
        }
    }

    @PutMapping("/{userId}/update-addresses")
    public ResponseEntity<Response<UserDTO>> updateAddresses(
            @PathVariable Long userId,
            @RequestBody UpdateAddressRequest request) {

        Optional<User> userOptional = userProfileService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<UserDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("User not found")
                            .build());
        }

        User user = userOptional.get();

        // Replace billing addresses if provided
        if (request.getBillingAddresses() != null) {
            user.getBillingAddresses().clear(); // Clear existing billing addresses
            List<Address> newBillingAddresses = request.getBillingAddresses()
                    .stream()
                    .map(addressDTO -> {
                        Address address = modelMapper.map(addressDTO, Address.class);
                        address.setUser(user); // Set the user reference
                        address.setAddressType(AddressType.BILLING);
                        return address;
                    })
                    .collect(Collectors.toList());
            user.getBillingAddresses().addAll(newBillingAddresses);
        }

        // Replace delivery addresses if provided
        if (request.getDeliveryAddresses() != null) {
            user.getDeliveryAddresses().clear(); // Clear existing delivery addresses
            List<Address> newDeliveryAddresses = request.getDeliveryAddresses()
                    .stream()
                    .map(addressDTO -> {
                        Address address = modelMapper.map(addressDTO, Address.class);
                        address.setUser(user); // Set the user reference
                        address.setAddressType(AddressType.DELIVERY);
                        return address;
                    })
                    .collect(Collectors.toList());
            user.getDeliveryAddresses().addAll(newDeliveryAddresses);
        }

        userProfileService.save(user);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(
                Response.<UserDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(userDTO)
                        .message("Addresses updated successfully")
                        .build());
    }

    @PostMapping("/{userId}/uploadProfilePic")
    public ResponseEntity<Response<User>> uploadProfilePi(
            @PathVariable Long userId,
            @RequestParam(value = "attachment") MultipartFile attachment){
        Response<User> response = userProfileService.uploadProfilePic(userId, attachment);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));

    }



}

