package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.otp.Otp;

import java.util.List;
import java.util.Optional;


@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    List<Otp> findAllByContactAndEmail(String contact, String email);

    Otp findByContactAndEmailAndOtpCodeOrderByCreatedAtDesc(String contact, String email, String otpCode);

    Optional<Otp> findTopByContactAndEmailOrderByCreatedAtDesc(String contact, String email);

    void deleteByContactAndEmail(String contact, String email);
}


