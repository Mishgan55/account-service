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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AccountDto> getAccountInfo(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Deposit money to account")
    @ApiResponse(responseCode = "200", description = "Deposit successful")
    @PostMapping("/deposit")
    public ResponseEntity<String> depositToAccount(
            @Parameter(description = "Deposit request", required = true) @RequestBody AccountDepositRequest accountDepositRequest) {
        accountService.depositWithRetry(accountDepositRequest);
        return ResponseEntity.ok(PropertyUtil.DEPOSIT_SUCCESSFUL);
    }

    @Operation(summary = "Get all accounts")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
    })
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @Operation(summary = "Edit account by ID")
    @ApiResponse(responseCode = "200", description = "Edit successful")
    @PatchMapping("/{id}/edit")
    public ResponseEntity<String> editAccount(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id,
            @Parameter(description = "Account details", required = true) @RequestBody AccountDto accountDto) {
        accountService.updateAccount(accountDto, id);
        return ResponseEntity.ok(PropertyUtil.EDIT_SUCCESSFUL);
    }

    @Operation(summary = "Delete account by ID")
    @ApiResponse(responseCode = "200", description = "Delete successful")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(
            @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(PropertyUtil.DELETE_SUCCESSFUL);
    }

    @Operation(summary = "Add new account")
    @ApiResponse(responseCode = "200", description = "Add successful")
    @PostMapping("/add")
    public ResponseEntity<String> addAccount(
            @Parameter(description = "Account details", required = true) @RequestBody AccountDto accountDto) {
        accountService.createAccount(accountDto);
        return ResponseEntity.ok(PropertyUtil.ADD_SUCCESSFUL);
    }

    @Operation(summary = "Get accounts by user ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))
    })
    @GetMapping("/accountsByUserId/{userId}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @Operation(summary = "Transfer money between accounts")
    @ApiResponse(responseCode = "200", description = "Transfer successful")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @Parameter(description = "Transfer request", required = true) @RequestBody TransferRequestModel model) {
        accountService.transferMoneyToAnotherUser(model);
        return ResponseEntity.ok(PropertyUtil.TRANSFER_SUCCESSFUL);
    }
}
