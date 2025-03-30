package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private Long id;
    private String code;
    private String description;
    private String organization;
    private Integer leadTimeStd;
    private Integer leadTimePriority;
    private Integer leadTimeMax;

    private List<City> cities;
}
