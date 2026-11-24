package com.bank.service.impl;

import com.bank.dto.request.CustomerRequestDTO;
import com.bank.dto.response.AccountSummaryResponseDTO;
import com.bank.dto.response.CustomerAccountsResponseDTO;
import com.bank.dto.response.CustomerResponseDTO;
import com.bank.entity.BankAccount;
import com.bank.entity.Customer;
import com.bank.exception.DuplicateCustomerException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.CustomerRepository;
import com.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO) {
        if (customerRepository.existsByEmail(customerRequestDTO.getEmail())) {
            throw new DuplicateCustomerException("Email already exists.");
        }

        if (customerRepository.existsByMobileNumber(customerRequestDTO.getMobileNumber())) {
            throw new DuplicateCustomerException("Mobile Number already exists.");
        }
        Customer customer = new Customer();
        customer.setCustomerName(customerRequestDTO.getCustomerName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setMobileNumber(customerRequestDTO.getMobileNumber());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setCreatedOn(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);

        return new CustomerResponseDTO(saved.getCustomerId(),
                saved.getCustomerName(),
                saved.getEmail(),
                "Customer registered successfully");

    }

    @Override
    public CustomerAccountsResponseDTO getCustomerAccounts(Integer customerId) {
        Optional<Customer> byIdWithAccounts = customerRepository.findByIdWithAccounts(customerId);
        if (byIdWithAccounts.isEmpty()) {
            throw new ResourceNotFoundException("Customer Not found with ID: " + customerId);
        }
        Customer customer = byIdWithAccounts.get();
        ArrayList<AccountSummaryResponseDTO> accountDTOs = new ArrayList<>();
        if (customer.getAccounts() != null) {
            for (BankAccount account : customer.getAccounts()) {
                AccountSummaryResponseDTO summaryResponseDTO = new AccountSummaryResponseDTO(account.getAccountId(), account.getAccountNumber(), account.getAccountType(), account.getBalance());
                accountDTOs.add(summaryResponseDTO);
            }
        }
        return new CustomerAccountsResponseDTO(customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getEmail(),
                accountDTOs);
    }
}
