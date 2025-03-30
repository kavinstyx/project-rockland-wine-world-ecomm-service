package com.commonlibrary.contract.v1.report;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class SelectedFilterDto {
    private String filterName;
    private String filterParameter;
    private String dataType; // Type of data (e.g., "select", "text", "date")
    private List<FilterOptionDto> options;
    private String value;
}
