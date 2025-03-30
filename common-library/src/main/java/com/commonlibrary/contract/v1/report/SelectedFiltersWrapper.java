package com.commonlibrary.contract.v1.report;

import lombok.Data;

import java.util.List;

@Data
public class SelectedFiltersWrapper {
    private List<SelectedFilterDto> filters;
}
