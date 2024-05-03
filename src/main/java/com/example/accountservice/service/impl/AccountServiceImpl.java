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
import com.example.accountservice.utill.PropertyUtil;
import com.example.accountservice.utill.account.AccountNotFoundException;
import com.example.accountservice.utill.account.AccountUpdateFailedException;
import com.example.accountservice.utill.enums.Currency;
import com.example.accountservice.utill.enums.OperationType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.json.JSONObject;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OperationService operationService;
    private final EntityManager entityManager;

    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountMapper accountMapper, OperationService operationService, EntityManager entityManager) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.operationService = operationService;
        this.entityManager = entityManager;
    }

    @Override
    public AccountDto getAccountById(Long id) {
        return accountMapper.toAccountDto(accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException(String.format(PropertyUtil.ACCOUNT_NOT_FOUND, id), LocalDateTime.now())));
    }

    @Override
    @Transactional
    public void transferMoneyToAnotherUser(TransferRequestModel transferRequestModel) {
        withDrawFromAccount(transferRequestModel.getFromAccount(), transferRequestModel.getAmount());
        depositToAccountWithOrNotCommission(transferRequestModel);
    }

    private void depositToAccountWithOrNotCommission(TransferRequestModel transferRequestModel) {
        Long toUserId = transferRequestModel.getToUser();
        BigDecimal amountToTransfer = transferRequestModel.getAmount();
        User user = new User();
        user.setId(toUserId);
        List<Account> recipientAccounts = accountRepository.findAccountsByUser(user);
        if (recipientAccounts.isEmpty()) {
            throw new AccountNotFoundException(String.format(PropertyUtil.ACCOUNTS_NOT_FOUND_FOR_USER, toUserId), LocalDateTime.now());
        }

        Optional<Account> optionalTargetAccount = recipientAccounts.stream()
                .filter(account -> account.getCurrency().equals(transferRequestModel.getCurrency()))
                .findAny();

        if (optionalTargetAccount.isPresent()) {
            AccountDepositRequest accountDepositRequest = new AccountDepositRequest();
            accountDepositRequest.setAccountId(optionalTargetAccount.get().getId());
            accountDepositRequest.setAmount(amountToTransfer);
            accountDepositRequest.setWithCommission(false);
            depositWithRetry(accountDepositRequest);
        } else {
            Account targetAccount = recipientAccounts.getFirst();
            AccountDepositRequest accountDepositRequest = new AccountDepositRequest();
            accountDepositRequest.setAccountId(targetAccount.getId());
            accountDepositRequest.setAmount(amountToTransfer);
            accountDepositRequest.setWithCommission(true);
            accountDepositRequest.setCurrencyFrom(transferRequestModel.getCurrency());
            accountDepositRequest.setCurrencyTo(targetAccount.getCurrency());
            depositWithRetry(accountDepositRequest);
        }
    }

    @Override
    @Transactional
    public void depositWithRetry(AccountDepositRequest accountDepositRequest) {
        int retries = 0;
        boolean success = false;

        while (!success && retries < PropertyUtil.MAX_RETRIES) {
            try {
                Account account = accountRepository.findById(accountDepositRequest.getAccountId())
                        .orElseThrow(() -> new AccountNotFoundException(String.format(PropertyUtil.ACCOUNT_NOT_FOUND,
                                accountDepositRequest.getAccountId()), LocalDateTime.now()));

                entityManager.lock(account, LockModeType.OPTIMISTIC);

                BigDecimal newBalance;
                if (!accountDepositRequest.getWithCommission()) {
                    newBalance = account.getBalance().add(accountDepositRequest.getAmount());
                } else {
                    BigDecimal convertedAmount = convertCurrency(accountDepositRequest);
                    BigDecimal commission = convertedAmount.multiply(PropertyUtil.COMMISSION_VALUE);
                    newBalance = account.getBalance().add(convertedAmount.subtract(commission));
                }
                account.setBalance(newBalance);

                accountRepository.save(account);
                success = true;

                operationService.createOperation(createOperationClass(OperationType.DEPOSIT, account, accountDepositRequest.getAmount()));
            } catch (OptimisticLockingFailureException e) {
                retries++;
            }
        }

        if (!success) {
            throw new AccountUpdateFailedException(String.format(PropertyUtil.FAILED_TO_UPDATE_MESSAGE, retries));
        }
    }

    private Operation createOperationClass(OperationType operationType, Account account, BigDecimal amount) {
        Operation operation = new Operation();
        operation.setAccount(account);
        operation.setAmount(amount);
        operation.setOperationType(operationType);
        operation.setTimesTamp(LocalDateTime.now());
        return operation;
    }

    private BigDecimal convertCurrency(AccountDepositRequest accountDepositRequest) {
        try {
            String currencyFrom = accountDepositRequest.getCurrencyFrom().toString();
            String currencyTo = accountDepositRequest.getCurrencyTo().toString();
            BigDecimal amount = accountDepositRequest.getAmount();
            HttpResponse<JsonNode> response = Unirest.get(PropertyUtil.OPEN_API_URL)
                    .asJson();

            JSONObject ratesObject = response.getBody().getObject().getJSONObject("rates");

            BigDecimal currencyValueFrom = BigDecimal.valueOf(ratesObject.getDouble(currencyFrom));
            BigDecimal currencyValueTo = BigDecimal.valueOf(ratesObject.getDouble(currencyTo));
            if (currencyFrom.equals(Currency.USD.toString())) {
                return amount.multiply(currencyValueTo);
            } else {
                return currencyValueFrom.multiply(amount.multiply(currencyValueTo));
            }
        } catch (UnirestException e) {
            throw new AccountUpdateFailedException(PropertyUtil.FAILED_TO_CONVERT);
        }
    }

    public void withDrawFromAccount(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format(PropertyUtil.ACCOUNT_NOT_FOUND, accountId), LocalDateTime.now()));

        entityManager.lock(account, LockModeType.OPTIMISTIC);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new AccountUpdateFailedException(String.format(PropertyUtil.NOT_ENOUGH_BALANCE, account.getId()));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

            operationService.createOperation(createOperationClass(OperationType.WITHDRAWAL, account, amount));
        }
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(accountMapper::toAccountDto)
                .toList();
    }

    @Transactional
    @Override
    public AccountDto updateAccount(AccountDto accountDto, Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(String.format(PropertyUtil.ACCOUNT_NOT_FOUND, id), LocalDateTime.now()));
        account.setBalance(accountDto.getBalance());
        account.setCurrency(accountDto.getCurrency());
        return accountDto;
    }

    @Transactional
    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        accountRepository.save(accountMapper.toAccount(accountDto));
        return accountDto;
    }

    @Override
    public List<AccountDto> getAccountsByUserId(Long userId) {
        User user = new User();
        user.setId(userId);
        return accountRepository
                .findAccountsByUser(user)
                .stream()
                .map(accountMapper::toAccountDto)
                .toList();
    }
}
