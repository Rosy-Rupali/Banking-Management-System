package com.bank.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransferRequestDTO {
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
    private String description;
}
