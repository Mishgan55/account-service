package com.example.accountservice.service.impl;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Operation;
import com.example.accountservice.entity.User;
import com.example.accountservice.mapper.AccountMapper;
import com.example.accountservice.model.AccountDepositRequest;
import com.example.accountservice.model.TransferRequestModel;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.service.OperationService;
import com.example.accountservice.utill.account.AccountNotFoundException;
import com.example.accountservice.utill.account.AccountUpdateFailedException;
import com.example.accountservice.utill.enums.Currency;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private OperationService operationService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AccountServiceImpl accountService;


    @Test
    public void testConcurrentTransfer() {

        Account account1 = new Account();
        account1.setId(1L);
        account1.setBalance(BigDecimal.valueOf(1000));
        account1.setCurrency(Currency.USD);

        Account account2 = new Account();
        account2.setId(2L);
        account2.setBalance(BigDecimal.valueOf(500));
        account2.setCurrency(Currency.USD);


        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));

        TransferRequestModel transferRequestModel1 = new TransferRequestModel();
        transferRequestModel1.setFromAccount(1L);
        transferRequestModel1.setToUser(2L);
        transferRequestModel1.setAmount(BigDecimal.valueOf(100));
        transferRequestModel1.setCurrency(Currency.USD);

        TransferRequestModel transferRequestModel2 = new TransferRequestModel();
        transferRequestModel2.setFromAccount(2L);
        transferRequestModel2.setToUser(1L);
        transferRequestModel2.setAmount(BigDecimal.valueOf(50));
        transferRequestModel2.setCurrency(Currency.USD);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        when(accountRepository.findAccountsByUser(user1)).thenReturn(List.of(account1));
        when(accountRepository.findAccountsByUser(user2)).thenReturn(List.of(account2));

        AccountService accountService = new AccountServiceImpl(accountRepository, null, operationService, entityManager);


        Thread thread1 = new Thread(() -> {
            accountService.transferMoneyToAnotherUser(transferRequestModel1);
        });
        Thread thread2 = new Thread(() -> {
            accountService.transferMoneyToAnotherUser(transferRequestModel2);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        assertEquals(BigDecimal.valueOf(950), account1.getBalance());
        assertEquals(BigDecimal.valueOf(550), account2.getBalance());
    }


    @Test
    public void testGetAccountById() {

        Long accountId = 1L;
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(1000.0));
        AccountDto expectedAccountDto = new AccountDto();
        expectedAccountDto.setBalance(BigDecimal.valueOf(1000.0));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(expectedAccountDto);

        AccountDto actualAccountDto = accountService.getAccountById(accountId);

        assertNotNull(actualAccountDto);
        assertEquals(expectedAccountDto.getBalance(), actualAccountDto.getBalance());
    }

    @Test
    public void testGetAccountById_AccountNotFound() {

        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(accountId);
        });
        assertEquals("Account not found with ID: 1", exception.getMessage());
    }

    @Test
    void testTransferMoneyToAnotherUser() {

        Long toUserId = 2L;
        TransferRequestModel transferRequestModel = new TransferRequestModel();
        transferRequestModel.setFromAccount(1L);
        transferRequestModel.setToUser(toUserId);
        transferRequestModel.setAmount(BigDecimal.valueOf(100.0));
        transferRequestModel.setCurrency(Currency.USD);


        Account fromAccount = new Account();
        fromAccount.setId(transferRequestModel.getFromAccount());
        fromAccount.setBalance(BigDecimal.valueOf(200.0));

        Account toAccount = new Account();
        toAccount.setId(1L);
        toAccount.setCurrency(Currency.USD);

        List<Account> recipientAccounts = new ArrayList<>();
        recipientAccounts.add(toAccount);

        when(accountRepository.findById(transferRequestModel.getFromAccount())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findAccountsByUser(any())).thenReturn(recipientAccounts);

        assertDoesNotThrow(() -> accountService.transferMoneyToAnotherUser(transferRequestModel));

        verify(accountRepository, times(1)).findAccountsByUser(any());
        verify(accountRepository, times(2)).save(any());
        verify(operationService, times(2)).createOperation(any(Operation.class));
    }

    @Test
    void testTransferMoneyToAnotherUser_InsufficientBalance() {

        TransferRequestModel transferRequestModel = new TransferRequestModel();
        transferRequestModel.setFromAccount(1L);
        transferRequestModel.setToUser(2L);
        transferRequestModel.setAmount(BigDecimal.valueOf(1000.0));
        transferRequestModel.setCurrency(Currency.USD);

        Account fromAccount = new Account();
        fromAccount.setId(transferRequestModel.getFromAccount());
        fromAccount.setBalance(BigDecimal.valueOf(500.0));

        when(accountRepository.findById(transferRequestModel.getFromAccount())).thenReturn(Optional.of(fromAccount));

        assertThrows(AccountUpdateFailedException.class, () -> accountService.transferMoneyToAnotherUser(transferRequestModel));
    }

    @Test
    void testDepositWithRetry() {

        AccountDepositRequest depositRequest = new AccountDepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(100.0));
        depositRequest.setWithCommission(false);

        Account account = new Account();
        account.setId(depositRequest.getAccountId());
        account.setBalance(BigDecimal.valueOf(100.0));

        when(accountRepository.findById(depositRequest.getAccountId())).thenReturn(Optional.of(account));

        assertDoesNotThrow(() -> accountService.depositWithRetry(depositRequest));

        verify(accountRepository, times(1)).findById(depositRequest.getAccountId());
        verify(accountRepository, times(1)).save(account);
        verify(operationService, times(1)).createOperation(any(Operation.class));
    }

    @Test
    void testDepositWithRetry_AccountNotFound() {

        AccountDepositRequest depositRequest = new AccountDepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(100.0));
        depositRequest.setWithCommission(false);

        when(accountRepository.findById(depositRequest.getAccountId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.depositWithRetry(depositRequest));
    }

    @Test
    void testDepositWithRetry_OptimisticLockingFailure() {

        AccountDepositRequest depositRequest = new AccountDepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(100.0));
        depositRequest.setWithCommission(false);

        Account account = new Account();
        account.setId(depositRequest.getAccountId());
        account.setBalance(BigDecimal.valueOf(100.0));

        when(accountRepository.findById(depositRequest.getAccountId())).thenReturn(Optional.of(account));
        doThrow(OptimisticLockingFailureException.class).when(accountRepository).save(account);

        assertThrows(AccountUpdateFailedException.class, () -> accountService.depositWithRetry(depositRequest));

        verify(accountRepository, times(5)).findById(depositRequest.getAccountId());
        verify(accountRepository, times(5)).save(account);
        verify(operationService, times(0)).createOperation(any(Operation.class));
    }


    @Test
    public void testGetAllAccounts() {

        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account());
        accounts.add(new Account());
        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.toAccountDto(any())).thenReturn(new AccountDto());

        List<AccountDto> accountDtos = accountService.getAllAccounts();

        assertNotNull(accountDtos);
        assertEquals(2, accountDtos.size());
    }
}