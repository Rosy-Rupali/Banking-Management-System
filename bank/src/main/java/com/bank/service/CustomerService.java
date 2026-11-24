package com.bank.service;

import com.bank.dto.request.CustomerRequestDTO;
import com.bank.dto.response.CustomerAccountsResponseDTO;
import com.bank.dto.response.CustomerResponseDTO;
import org.springframework.stereotype.Service;

public interface CustomerService {
    CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO);

    CustomerAccountsResponseDTO getCustomerAccounts(Integer customerId);
}
