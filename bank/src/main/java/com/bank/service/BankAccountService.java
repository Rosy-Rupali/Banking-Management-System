package com.bank.service;

import com.bank.dto.request.BankAccountRequestDTO;
import com.bank.dto.request.DepositRequestDTO;
import com.bank.dto.request.TransferRequestDTO;
import com.bank.dto.request.WithdrawRequestDTO;
import com.bank.dto.response.*;

public interface BankAccountService {
    BankAccountResponseDTO openAccount(BankAccountRequestDTO bankAccountRequestDTO);

    DepositResponseDTO depositAmount(Integer accountId, DepositRequestDTO depositRequestDTO);

    WithdrawResponseDTO withdrawAmount(Integer accountId, WithdrawRequestDTO withdrawRequestDTO);

    TransferResponseDTO transferAmount(TransferRequestDTO transferRequestDTO);

    TransactionHistoryResponseDTO getTransactionHistory(Integer accountId);
}
