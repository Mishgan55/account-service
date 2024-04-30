package com.example.accountservice.mapper;

import com.example.accountservice.dto.OperationDto;
import com.example.accountservice.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OperationMapper {
    @Mapping(target = "account.id", source = "accountId")
    Operation toOperation(OperationDto operationDto);

    @Mapping(target = "accountId", source = "account.id")
    OperationDto toOperationDto(Operation operation);
}
