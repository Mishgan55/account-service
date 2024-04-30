package com.example.accountservice.utill.account;

import com.example.accountservice.utill.user.UserDuplicateDocumentTypeAndNumberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountNotFoundExceptionHandler {
    @ExceptionHandler({AccountNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleAccountNotFoundException(UserDuplicateDocumentTypeAndNumberException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage()+", "+ex.getDate());
    }

}
