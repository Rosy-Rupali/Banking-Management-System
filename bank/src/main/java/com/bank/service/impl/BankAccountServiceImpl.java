package com.bank.service.impl;

import com.bank.dto.request.BankAccountRequestDTO;
import com.bank.dto.request.DepositRequestDTO;
import com.bank.dto.request.TransferRequestDTO;
import com.bank.dto.request.WithdrawRequestDTO;
import com.bank.dto.response.*;
import com.bank.entity.BankAccount;
import com.bank.entity.BankTransaction;
import com.bank.entity.Customer;
import com.bank.enums.AccountStatus;
import com.bank.enums.AccountType;
import com.bank.enums.TransactionType;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAccountException;
import com.bank.exception.InvalidInputException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.projection.TransactionProjection;
import com.bank.repository.BankAccountRepository;
import com.bank.repository.BankTransactionRepository;
import com.bank.repository.CustomerRepository;
import com.bank.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository bankTransactionRepository;

    //    @Transactional
    @Override
    public BankAccountResponseDTO openAccount(BankAccountRequestDTO bankAccountRequestDTO) {
        if (bankAccountRequestDTO.getCustomerId() == null) {
            throw new InvalidInputException("Customer ID is required.");
        }
        if (bankAccountRequestDTO.getAccountType() == null) {
            throw new InvalidInputException("Account Type is required.");
        }
        if (bankAccountRequestDTO.getInitialDeposit() == null) {
            throw new InvalidInputException("Initial deposit is required.");
        }
        if (bankAccountRequestDTO.getAccountType() == AccountType.SAVINGS && bankAccountRequestDTO.getInitialDeposit().compareTo(new BigDecimal("5000")) < 0) {
            throw new InsufficientBalanceException("Minimum balance for SAVINGS should be 5000.");
        } else if (bankAccountRequestDTO.getAccountType() == AccountType.CURRENT && bankAccountRequestDTO.getInitialDeposit().compareTo(new BigDecimal("1000")) < 0) {
            {
                throw new InsufficientBalanceException("Minimum balance for SAVINGS should be 1000.");
            }
        }

        Optional<Customer> byId = customerRepository.findById(bankAccountRequestDTO.getCustomerId());
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id " + bankAccountRequestDTO.getCustomerId() + " does not exist.");
        }
        Customer customer = byId.get();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("Bank" + System.currentTimeMillis());
        bankAccount.setAccountType(bankAccountRequestDTO.getAccountType());
        bankAccount.setBalance(bankAccountRequestDTO.getInitialDeposit());
        bankAccount.setAccountStatus(AccountStatus.ACTIVE);
        bankAccount.setCreatedOn(LocalDateTime.now());
        bankAccount.setCustomer(customer);
        BankAccount saved = bankAccountRepository.save(bankAccount);

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setTransactionReference("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bankTransaction.setTransactionType(TransactionType.CREDIT);
        bankTransaction.setAmount(bankAccountRequestDTO.getInitialDeposit());
        bankTransaction.setTransactionDate(LocalDateTime.now());
        bankTransaction.setCreatedOn(LocalDateTime.now());
        bankTransaction.setDescription("Initial Deposit");
        bankTransaction.setAccount(saved);

        bankTransactionRepository.save(bankTransaction);

        return new BankAccountResponseDTO(saved.getAccountId(),
                saved.getAccountNumber(),
                saved.getAccountType(),
                saved.getBalance(),
                saved.getAccountStatus(),
                "Bank is created successfully");
    }

    @Override
    @Transactional
    public DepositResponseDTO depositAmount(Integer accountId, DepositRequestDTO depositRequestDTO) {
        //validate the deposit amount
        if (depositRequestDTO.getAmount() == null || depositRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Deposit amount must be greater than 0");
        }
        // Find bank account id
        Optional<BankAccount> byId = bankAccountRepository.findById(accountId);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("Bank account not with found ID: " + accountId);
        }
        //find the account is active or not
        BankAccount account = byId.get();
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountException("Account is INACTIVE");
        }
        BigDecimal updatedBalance = account.getBalance().add(depositRequestDTO.getAmount());
        account.setBalance(updatedBalance);
        account.setUpdatedBy("SYSTEM");
        account.setUpdatedOn(LocalDateTime.now());
        bankAccountRepository.save(account);

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setTransactionReference("TXN" + UUID.randomUUID().toString());
        bankTransaction.setAmount(depositRequestDTO.getAmount());
        bankTransaction.setTransactionDate(LocalDateTime.now());
        bankTransaction.setTransactionType(TransactionType.CREDIT);
        bankTransaction.setAccount(account);
        bankTransaction.setDescription(depositRequestDTO.getDescription() != null && !depositRequestDTO.getDescription().isEmpty() ? depositRequestDTO.getDescription() : "Cash deposit");
        bankTransaction.setCreatedBy("SYSTEM");
        bankTransaction.setUpdatedBy("SYSTEM");
        bankTransaction.setUpdatedOn(LocalDateTime.now());
        bankTransaction.setCreatedOn(LocalDateTime.now());
        bankTransactionRepository.save(bankTransaction);

        return new DepositResponseDTO(account.getAccountId(), depositRequestDTO.getAmount(), account.getBalance(), bankTransaction.getTransactionType(), "Amount deposited successfully");
    }

    @Override
    @Transactional
    public WithdrawResponseDTO withdrawAmount(Integer accountId, WithdrawRequestDTO withdrawRequestDTO) {
        //validate the deposit amount
        if (withdrawRequestDTO.getAmount() == null || withdrawRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Deposit amount must be greater than 0");
        }
        // Find bank account id
        Optional<BankAccount> byId = bankAccountRepository.findByIdWithLock(accountId);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("Bank account not with found ID: " + accountId);
        }
        //find the account is active or not
        BankAccount account = byId.get();
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountException("Account is INACTIVE");
        }
        //Prevent negative balance (Insufficient balance check)
        if (account.getBalance().compareTo(withdrawRequestDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available balance: " + account.getBalance());
        }
        BigDecimal updatedBalance = account.getBalance().subtract(withdrawRequestDTO.getAmount());
        account.setBalance(updatedBalance);
        account.setUpdatedBy("SYSTEM");
        account.setUpdatedOn(LocalDateTime.now());
        bankAccountRepository.save(account);

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setTransactionReference("TXN" + UUID.randomUUID().toString());
        bankTransaction.setAmount(withdrawRequestDTO.getAmount());
        bankTransaction.setTransactionDate(LocalDateTime.now());
        bankTransaction.setTransactionType(TransactionType.DEBIT);
        bankTransaction.setAccount(account);
        bankTransaction.setDescription(withdrawRequestDTO.getDescription() != null && !withdrawRequestDTO.getDescription().isEmpty() ? withdrawRequestDTO.getDescription() : "Cash withdrawn");
        bankTransaction.setCreatedBy("SYSTEM");
        bankTransaction.setUpdatedBy("SYSTEM");
        bankTransaction.setUpdatedOn(LocalDateTime.now());
        bankTransaction.setCreatedOn(LocalDateTime.now());
        bankTransactionRepository.save(bankTransaction);

        return new WithdrawResponseDTO(account.getAccountId(), bankTransaction.getTransactionType(), account.getBalance(), withdrawRequestDTO.getAmount(), "Amount withdrawn successfully");
    }

    @Override
    @Transactional
    public TransferResponseDTO transferAmount(TransferRequestDTO transferRequestDTO) {
        if (transferRequestDTO.getFromAccountId() == null || transferRequestDTO.getToAccountId() == null) {
            throw new InvalidInputException("Both source and destination account ID's are required");
        }
        if (transferRequestDTO.getFromAccountId().equals(transferRequestDTO.getToAccountId())) {
            throw new InvalidAccountException("Both source and destination accountID's are same");
        }
        if (transferRequestDTO.getAmount() == null || transferRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Transfer amount must be greater than zero.");
        }
        Optional<BankAccount> fromIdWithLock = bankAccountRepository.findByIdWithLock(transferRequestDTO.getFromAccountId());
        Optional<BankAccount> toIdWithLock = bankAccountRepository.findByIdWithLock(transferRequestDTO.getToAccountId());
        if(fromIdWithLock.isEmpty()){
            throw new ResourceNotFoundException("Source account ID: "+transferRequestDTO.getFromAccountId()+" not found!!");
        }
        if(toIdWithLock.isEmpty()){
            throw new ResourceNotFoundException("Destination account ID: "+transferRequestDTO.getToAccountId()+" not found!!");
        }
        BankAccount fromAccount = fromIdWithLock.get();
        BankAccount toAccount = toIdWithLock.get();
        if(fromAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new InvalidAccountException("Source Account is "+fromAccount.getAccountStatus());
        }
        if(toAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new InvalidAccountException("Destination Account is "+toAccount.getAccountStatus());
        }
        if(fromAccount.getBalance().compareTo(transferRequestDTO.getAmount())<0){
            throw new InsufficientBalanceException("Insufficient balance. Available: "+fromAccount.getBalance());
        }
        String description = (transferRequestDTO.getDescription() != null && !transferRequestDTO.getDescription().isEmpty() ? transferRequestDTO.getDescription() : "Fund Transfer");
        BigDecimal senderAvailableBalance = fromAccount.getBalance().subtract(transferRequestDTO.getAmount());
        fromAccount.setBalance(senderAvailableBalance);
        fromAccount.setUpdatedBy("SYSTEM");
        fromAccount.setUpdatedOn(LocalDateTime.now());
        bankAccountRepository.save(fromAccount);

        BigDecimal receiverBalance = toAccount.getBalance().add(transferRequestDTO.getAmount());
        toAccount.setBalance(receiverBalance);
        toAccount.setUpdatedBy("SYSTEM");
        toAccount.setUpdatedOn(LocalDateTime.now());
        bankAccountRepository.save(toAccount);

        BankTransaction debitTxn = new BankTransaction();
        debitTxn.setTransactionReference("TXN"+UUID.randomUUID().toString());
        debitTxn.setTransactionType(TransactionType.DEBIT);
        debitTxn.setTransactionDate(LocalDateTime.now());
        debitTxn.setDescription(description);
        debitTxn.setAccount(fromAccount);
        debitTxn.setAmount(transferRequestDTO.getAmount());
        debitTxn.setCreatedOn(LocalDateTime.now());
        debitTxn.setUpdatedOn(LocalDateTime.now());
        debitTxn.setUpdatedBy("SYSTEM");
        bankTransactionRepository.save(debitTxn);

        BankTransaction creditTxn = new BankTransaction();
        creditTxn.setTransactionReference("TXN"+UUID.randomUUID().toString());
        creditTxn.setTransactionType(TransactionType.CREDIT);
        creditTxn.setTransactionDate(LocalDateTime.now());
        creditTxn.setAmount(transferRequestDTO.getAmount());
        creditTxn.setAccount(toAccount);
        creditTxn.setDescription(description);
        creditTxn.setCreatedOn(LocalDateTime.now());
        creditTxn.setUpdatedOn(LocalDateTime.now());
        creditTxn.setUpdatedBy("SYSTEM");
        bankTransactionRepository.save(creditTxn);

        return new TransferResponseDTO(fromAccount.getAccountId(),toAccount.getAccountId(),fromAccount.getBalance(),toAccount.getBalance(),"Fund transferred Successfully");
    }

    @Override
    public TransactionHistoryResponseDTO getTransactionHistory(Integer accountId) {
        Optional<BankAccount> byId = bankAccountRepository.findById(accountId);
        if(byId.isEmpty()){
            throw new ResourceNotFoundException("Bank account not found with ID: "+accountId);
        }
        BankAccount account = byId.get();
        List<TransactionProjection> transactionDateDesc = bankTransactionRepository.findByAccountAccountIdOrderByTransactionDateDesc(accountId);
        return new TransactionHistoryResponseDTO(account.getAccountId(),account.getAccountNumber(), transactionDateDesc);
    }
}
