package com.example.accountservice.utill.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountNotFoundException extends RuntimeException {
    private final String message;
    private final LocalDateTime date;
}
