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
@Table(name = "channel_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditLogListener.class)
public class ChannelMaster extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    private String organization;

    private Integer leadTimeStd;
    private Integer leadTimePriority;
    private Integer leadTimeMax;

    @OneToMany(mappedBy = "channelMaster", cascade = CascadeType.ALL)
    private List<CityMaster> cities;

}
