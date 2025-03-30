package com.commonlibrary.contract.v1.report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FilterDto {
    private String filterName;
    private String filterParameter;
    private String dataType; // Type of data (e.g., "select", "text", "date")
    private List<FilterOptionDto> options;
    private String placeHolder;

    public FilterDto(String filterName, String filterParameter, String dataType, List<FilterOptionDto> options, String placeHolder) {
        this.filterName = filterName;
        this.filterParameter = filterParameter;
        this.dataType = dataType;
        this.options = options;
        this.placeHolder = placeHolder;
    }

    public boolean isValid() {
        if (filterName == null || filterParameter == null || dataType == null) {
            return false;
        }

        if (options != null) {
            options = options.stream()
                    .filter(FilterOptionDto::isValid)
                    .collect(Collectors.toList());
        }

        return true;
    }
}
