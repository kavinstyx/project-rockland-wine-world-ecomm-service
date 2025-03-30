package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private Long id;
    private String cityName;
    private String cityCode;
    private String province;
    private Boolean deliveryEnabled;

    private List<Plant> plants;
    private Long channelId;
    private List<DeliveryCharges> deliverChages;


}
