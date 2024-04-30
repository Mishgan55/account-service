package com.example.accountservice.utill.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountUpdateFailedExceptionHandler {
    @ExceptionHandler({AccountUpdateFailedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleAccountUpdateFailedException(AccountUpdateFailedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
