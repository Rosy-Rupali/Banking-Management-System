package com.bank.dto.response;

import com.bank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositResponseDTO {
    private Integer acoountId;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private TransactionType transactionType;
    private String message;
}
