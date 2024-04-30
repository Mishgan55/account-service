package com.example.accountservice.service;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.model.AccountDepositRequest;
import com.example.accountservice.model.TransferRequestModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {
    AccountDto getAccountById(Long id);

    void depositWithRetry(AccountDepositRequest accountDepositRequest);

    List<AccountDto> getAllAccounts();

    void updateAccount(AccountDto accountDto, Long id);

    void deleteAccount(Long id);

    void createAccount(AccountDto accountDto);

    List<AccountDto> getAccountsByUserId(Long userId);

    void transferMoneyToAnotherUser(TransferRequestModel model);
}
