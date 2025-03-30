package rockland.elysiancrest.com.data_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

@Entity
@Table(name = "subscribed_emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class SubscribeEmail extends BaseEntity{
    @Column(name = "email")
    private String email;
}
