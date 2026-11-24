package com.bank.controller;

import com.bank.constants.Constants;
import com.bank.dto.request.CustomerRequestDTO;
import com.bank.dto.response.CustomerAccountsResponseDTO;
import com.bank.dto.response.CustomerResponseDTO;
import com.bank.dto.response.ResponseDTO;
import com.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerCustomer(@RequestBody CustomerRequestDTO customerRequestDTO){
        CustomerResponseDTO customerResponseDTO = customerService.registerCustomer(customerRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_201, Constants.MESSAGE_201, customerResponseDTO);
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<ResponseDTO> getCustomerAccounts(@PathVariable Integer customerId){
        CustomerAccountsResponseDTO customerAccountsResponseDTO = customerService.getCustomerAccounts(customerId);
        ResponseDTO responseDTO = new ResponseDTO(Constants.STATUS_200,Constants.MESSAGE_200,customerAccountsResponseDTO);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }
}
