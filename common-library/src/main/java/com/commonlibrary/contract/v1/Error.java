package com.commonlibrary.contract.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
    private String error;
    private String code;
}

