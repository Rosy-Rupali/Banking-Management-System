package com.bank.projection;

import com.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionProjection {
    String getTransactionReference();
    TransactionType getTransactionType();
    BigDecimal getAmount();
    String getDescription();
    LocalDateTime getTransactionDate();
}
