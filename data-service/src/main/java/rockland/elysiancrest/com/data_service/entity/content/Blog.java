package rockland.elysiancrest.com.data_service.entity.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

@Entity
@Table(name = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class Blog extends BaseEntity {

    private String title;

    @Column(unique = true)
    private String slug;

    @Column(length = 1000)
    private String imageUrl;
    
    @Column(length = 100000, columnDefinition = "TEXT")
    private String content; // Stores HTML content
    
    private Boolean published = false;
    
}
