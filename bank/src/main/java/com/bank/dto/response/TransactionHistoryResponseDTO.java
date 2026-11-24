package com.bank.dto.response;

import com.bank.projection.TransactionProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class TransactionHistoryResponseDTO {
    private Integer accountId;
    private String accountNumber;
    private List<TransactionProjection> transactions;
}
