package rockland.elysiancrest.com.data_service.entity.master_data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.util.List;
import java.util.Set;


@Entity
@Table(name = "department_master")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditLogListener.class)
public class DepartmentMaster extends BaseEntity {

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @OneToMany(mappedBy = "department",cascade = CascadeType.ALL)
    private List<ProductMaster> products;
}

