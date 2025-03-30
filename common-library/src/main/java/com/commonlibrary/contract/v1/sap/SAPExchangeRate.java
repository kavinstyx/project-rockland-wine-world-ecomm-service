package com.commonlibrary.contract.v1.sap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAPExchangeRate {

    private String kurst;            // Exchange rate type (Length: 4)
    private String fcurr;            // From currency (Length: 5)
    private String tcurr;            // To currency (Length: 5)
    private LocalDate gdatu;         // Date the exchange rate is effective (Format: dd.MM.yyyy)
    private Double ukurs;            // Exchange rate (Length: 9)
}
