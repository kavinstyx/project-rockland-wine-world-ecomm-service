package rockland.elysiancrest.com.data_service.entity.master_data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.util.List;

@Entity
@Table(name = "plant_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class PlantMaster extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String plantCode;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(unique = true)
    private String email;

    private String contactNumber;

    @Column(nullable = false)
    private boolean webSalesEnabled;

    private String licenseNumber;

    private String licenseExpiryStatus;

    private String licenseOwner;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityMaster city;

    @OneToMany(mappedBy = "plantMaster")
    private List<Inventory> inventory;
}
