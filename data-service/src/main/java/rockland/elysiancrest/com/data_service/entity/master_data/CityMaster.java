package rockland.elysiancrest.com.data_service.entity.master_data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "city_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class CityMaster extends BaseEntity {

    @Column(nullable = false)
    private String cityName;

    @Column(nullable = false)
    private String cityCode;

    @Column(nullable = false)
    private String province;

    @Column(nullable = true, name = "delivery_enable")
    private Boolean deliveryEnabled;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PlantMaster> plants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = true)
    private ChannelMaster channelMaster;

    @OneToMany(mappedBy = "cityMaster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DeliveryChargesMaster> deliverCharges;

    @OneToMany(mappedBy = "cityMaster")
    private List<CartMaster> carts;
}
