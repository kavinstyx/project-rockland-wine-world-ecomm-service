package rockland.elysiancrest.com.data_service.entity.config;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.util.Date;

@Entity
@Table(name = "holiday")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class Holiday extends BaseEntity {
    
    @Column(name = "date", nullable = false, unique = true)
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @Column(name = "holiday_type", nullable = false)
    private String holidayType;
    
    @Column(name = "pickup_block")
    private Boolean pickupBlock;
    
    @Column(name = "delivery_block") 
    private Boolean deliveryBlock;
    
    @Column(name = "description", length = 1000)
    private String description;
}
