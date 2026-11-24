package com.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class TransferResponseDTO {
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal transferredAmount;
    private BigDecimal senderRemainingBalance;
    private String message;
}
