package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private Long id;
    private String exchangeRateType;
    private Double exRate;
    private LocalDate startDate;
    private String fromCurrency;
    private String toCurrency;
}
