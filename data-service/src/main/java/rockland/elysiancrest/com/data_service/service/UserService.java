package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Response;
import org.springframework.data.domain.Page;
import rockland.elysiancrest.com.data_service.data.LoginRequest;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.entity.User;

public interface UserService extends BaseService<User, UserDTO> {
    UserDTO convertToDto(User user);

    User convertToEntity(UserDTO userDTO);

    Response<UserDTO> registerUser(UserDTO userDTO);

    UserDTO loginUser(LoginRequest loginRequest);

    Response<UserDTO> markUserAsVerified(String contact, String email);

    User findByEmail(String email);

    Page<UserDTO> getUsersWithPagination(int page, int size, String searchString);

    Response<Void> toggleUserActivation(Long userId);

    UserDTO getUserById(Long userId);

    Response<UserDTO> updateUser(Long userId, UserDTO userDTO);
}
