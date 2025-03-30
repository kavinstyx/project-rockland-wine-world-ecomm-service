package com.commonlibrary.contract.v1.report;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HeaderDto {
    private String name;
    private String field;

    public HeaderDto(String name, String field) {
        this.name = name;
        this.field = field;
    }
} 