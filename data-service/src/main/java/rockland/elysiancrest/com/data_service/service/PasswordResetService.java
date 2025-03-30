package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Response;
import rockland.elysiancrest.com.data_service.dto.PasswordResetTokenDTO;
import rockland.elysiancrest.com.data_service.entity.PasswordResetToken;

public interface PasswordResetService extends BaseService<PasswordResetToken, PasswordResetTokenDTO> {

    PasswordResetTokenDTO convertToDto(PasswordResetToken passwordResetToken);

    PasswordResetToken convertToEntity(PasswordResetTokenDTO passwordResetTokenDTO);

    Response<String> resetPassword(String email, String otpCode, String newPassword);

    Response<String> sendPasswordResetEmail(String email);

    }
