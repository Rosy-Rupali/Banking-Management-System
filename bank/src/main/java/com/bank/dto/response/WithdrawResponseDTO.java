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
public class WithdrawResponseDTO {
    private Integer accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private String message;
}
