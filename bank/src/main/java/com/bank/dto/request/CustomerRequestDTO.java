package com.bank.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CustomerRequestDTO {
    private String customerName;
    private String email;
    private String mobileNumber;
    private String address;
}
