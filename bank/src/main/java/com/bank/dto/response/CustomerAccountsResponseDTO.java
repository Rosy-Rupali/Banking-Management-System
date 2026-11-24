package com.bank.dto.response;

import com.bank.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CustomerAccountsResponseDTO {
    private Integer customerId;
    private String customerName;
    private String email;
    private List<AccountSummaryResponseDTO> accounts;
}
