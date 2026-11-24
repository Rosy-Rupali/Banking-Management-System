package com.bank.controller;

import com.bank.constants.Constants;
import com.bank.dto.request.BankAccountRequestDTO;
import com.bank.dto.request.DepositRequestDTO;
import com.bank.dto.request.TransferRequestDTO;
import com.bank.dto.request.WithdrawRequestDTO;
import com.bank.dto.response.*;
import com.bank.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankController {

    private final BankAccountService bankAccountService;

    @PostMapping("/openaccount")
    public ResponseEntity<ResponseDTO> openAccount(@RequestBody BankAccountRequestDTO bankAccountRequestDTO) {
        BankAccountResponseDTO bankAccountResponseDTO = bankAccountService.openAccount(bankAccountRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_201, Constants.MESSAGE_201, bankAccountResponseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<ResponseDTO> depositAmount(@PathVariable Integer accountId, @RequestBody DepositRequestDTO depositRequestDTO) {
        DepositResponseDTO depositResponseDTO = bankAccountService.depositAmount(accountId, depositRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_201, Constants.MESSAGE_201, depositResponseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<ResponseDTO> withdrawAmount(@PathVariable Integer accountId, @RequestBody WithdrawRequestDTO withdrawRequestDTO){
        WithdrawResponseDTO withdrawResponseDTO = bankAccountService.withdrawAmount(accountId, withdrawRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_200, Constants.MESSAGE_200, withdrawResponseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.ACCEPTED);
    }
    @PostMapping("/transfer")
    public ResponseEntity<ResponseDTO> transferAmount( @RequestBody TransferRequestDTO transferRequestDTO){
        TransferResponseDTO transferResponseDTO = bankAccountService.transferAmount(transferRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_200, Constants.MESSAGE_200, transferResponseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.ACCEPTED);
    }
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<ResponseDTO> getTransactionHistory(@PathVariable Integer accountId){
        TransactionHistoryResponseDTO historyResponseDTO = bankAccountService.getTransactionHistory(accountId);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_200, Constants.MESSAGE_200, historyResponseDTO);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }
}
