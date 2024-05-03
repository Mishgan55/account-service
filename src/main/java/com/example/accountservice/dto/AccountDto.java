package com.example.accountservice.dto;

import com.example.accountservice.utill.enums.Currency;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode
public class AccountDto {
    @NotEmpty(message = "User id should not be empty!")
    private Long userId;
    @NotEmpty(message = "Balance should not be empty!")
    private BigDecimal balance;
    @NotEmpty(message = "Currency should not be empty!")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid currency")
    @Size(min = 1, max = 50, message = "Name's characters should be between 1 and 50")
    private Currency currency;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
