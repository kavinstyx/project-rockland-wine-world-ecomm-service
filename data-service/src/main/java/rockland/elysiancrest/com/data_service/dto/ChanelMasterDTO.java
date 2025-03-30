package rockland.elysiancrest.com.data_service.dto;

import com.commonlibrary.contract.v1.City;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChanelMasterDTO {
    private Long id;
    private String code;
    private String description;
    private String organization;
    private Integer leadTimeStd;
    private Integer leadTimePriority;
    private Integer leadTimeMax;


    private List<City> cities;

}
