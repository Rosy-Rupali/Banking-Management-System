package com.bank.dto.response;

import com.bank.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerResponseDTO {
    private Integer customerId;
    private String customerName;
    private String email;
    private String message;
}
