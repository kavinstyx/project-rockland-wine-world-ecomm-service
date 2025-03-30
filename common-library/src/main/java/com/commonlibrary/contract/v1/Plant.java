package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    private Long id;
    private String plantCode;
    private String name;
    private String address;
    private String email;
    private String contactNumber;
    private boolean webSalesEnabled;
    private String licenseNumber;
    private String licenseExpiryStatus;
    private String licenseOwner;

    private Long cityId;
}
