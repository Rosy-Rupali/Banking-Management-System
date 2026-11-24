package com.bank.dto.request;

import com.bank.enums.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BankAccountRequestDTO {
    private Integer customerId;
    private AccountType accountType;
    private BigDecimal initialDeposit;
}
