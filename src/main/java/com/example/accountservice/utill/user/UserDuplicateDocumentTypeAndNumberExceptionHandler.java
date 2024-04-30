package com.example.accountservice.utill.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserDuplicateDocumentTypeAndNumberExceptionHandler {
    @ExceptionHandler({UserDuplicateDocumentTypeAndNumberException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDuplicateUserNumber(UserDuplicateDocumentTypeAndNumberException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage()+", "+ex.getDate());
    }
}