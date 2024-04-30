package com.example.accountservice.model;

import com.example.accountservice.utill.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDepositRequest {
    private Long accountId;
    private BigDecimal amount;
    private Boolean withCommission;
    private Currency currencyFrom;
    private Currency currencyTo;
}
