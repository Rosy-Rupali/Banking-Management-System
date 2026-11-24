package com.bank.repository;

import com.bank.dto.response.TransactionHistoryResponseDTO;
import com.bank.entity.BankTransaction;
import com.bank.projection.TransactionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Integer> {
    List<TransactionProjection> findByAccountAccountIdOrderByTransactionDateDesc(Integer accountId);
}
