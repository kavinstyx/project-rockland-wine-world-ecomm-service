package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id; // Unique identifier for the user
    private String name;
    private String address;
    private String username;
//    private String password; // You might not want to expose the password, so this could be omitted
    private String email;
    private String token;

    private String customerCode;
    private String contactNumbers;
    private String billingAddress;
    private String deliveryAddress;
    private String nic;
    private LocalDate dateOfBirth;
    private String nationality;
    private String liveIn;
    private String customerGroupType; // Example values: "Corporate", "Retail", etc.
    private String creditTerm;
    private String deliveryPlant;
    private boolean isEnabled;
    private boolean isVerified;

    private String profilePicPath;
    private String profilePicTemporaryLink;



    private List<Long> cartIds; // List of cart IDs the user owns (just the IDs or CartDTO if you need more detail)
    private List<Long> complaintIds;

}
