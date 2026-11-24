package com.bank.dto.response;

import com.bank.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class AccountSummaryResponseDTO {
    private Integer accountId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
}
