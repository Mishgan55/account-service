package com.example.accountservice.controller;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.model.AccountDepositRequest;
import com.example.accountservice.model.TransferRequestModel;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.utill.PropertyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get account by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccountInfo(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @Operation(summary = "Deposit money to account")
    @ApiResponse(responseCode = "200", description = "Deposit successful")
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public String depositToAccount(
            @Parameter(description = "Deposit request", required = true) @RequestBody AccountDepositRequest accountDepositRequest) {
        accountService.depositWithRetry(accountDepositRequest);
        return PropertyUtil.DEPOSIT_SUCCESSFUL;
    }

    @Operation(summary = "Get all accounts")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @Operation(summary = "Edit account by ID")
    @ApiResponse(responseCode = "200", description = "Edit successful")
    @PatchMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto editAccount(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id,
            @Parameter(description = "Account details", required = true) @RequestBody AccountDto accountDto) {
        return accountService.updateAccount(accountDto, id);
    }

    @Operation(summary = "Delete account by ID")
    @ApiResponse(responseCode = "200", description = "Delete successful")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAccount(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        accountService.deleteAccount(id);
        return PropertyUtil.DELETE_SUCCESSFUL;
    }

    @Operation(summary = "Add new account")
    @ApiResponse(responseCode = "201", description = "Add successful")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto addAccount(
            @Parameter(description = "Account details", required = true) @RequestBody @Valid AccountDto accountDto) {
        return accountService.createAccount(accountDto);
    }

    @Operation(summary = "Get accounts by user ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accountsByUserId/{userId}")
    public List<AccountDto> getAccountsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        return accountService.getAccountsByUserId(userId);
    }

    @Operation(summary = "Transfer money between accounts")
    @ApiResponse(responseCode = "200", description = "Transfer successful")
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public String transferMoney(
            @Parameter(description = "Transfer request", required = true) @RequestBody @Valid TransferRequestModel model) {
        accountService.transferMoneyToAnotherUser(model);
        return PropertyUtil.TRANSFER_SUCCESSFUL;
    }
}
