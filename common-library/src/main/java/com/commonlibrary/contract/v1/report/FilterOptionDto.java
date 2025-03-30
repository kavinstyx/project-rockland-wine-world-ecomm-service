package com.commonlibrary.contract.v1.report;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterOptionDto {
    private String code;
    private String name;

    public FilterOptionDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean isValid() {
        return code != null && name != null;
    }
}
