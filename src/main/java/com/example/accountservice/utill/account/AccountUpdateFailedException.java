package com.example.accountservice.utill.account;

public class AccountUpdateFailedException extends RuntimeException{
    public AccountUpdateFailedException(String message) {
        super(message);
    }
}
