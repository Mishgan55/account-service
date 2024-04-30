package com.example.accountservice.model;

import com.example.accountservice.utill.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestModel {
    private Long fromAccount;
    private Long toUser;
    private BigDecimal amount;
    private Currency currency;
}
