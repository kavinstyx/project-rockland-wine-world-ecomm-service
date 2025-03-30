package com.commonlibrary.contract.v1;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseResponse {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
