package com.commonlibrary.contract.v1.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ReportResponse<T>{
    List<HeaderDto> headers;
    Page<T> reportData;
    Map<String, Long> totals;
}
