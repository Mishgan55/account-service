package com.example.accountservice.utill.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDuplicateDocumentTypeAndNumberException extends RuntimeException{
    private final String message;
    private final LocalDateTime date;
}
