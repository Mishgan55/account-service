package com.example.accountservice.mapper;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "user.id", source = "userId")
    Account toAccount(AccountDto accountDto);

    @Mapping(target = "userId", source = "user.id")
    AccountDto toAccountDto(Account account);
}
