package rockland.elysiancrest.com.data_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pwd_reset_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken extends BaseEntity {

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Specifies the foreign key
    private User user;

    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}

