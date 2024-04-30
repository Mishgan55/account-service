package com.example.accountservice.service.impl;

import com.example.accountservice.dto.OperationDto;
import com.example.accountservice.entity.Operation;
import com.example.accountservice.mapper.OperationMapper;
import com.example.accountservice.repository.OperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceImplTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationMapper operationMapper;

    @InjectMocks
    private OperationServiceImpl operationService;


    @Test
    void getOperationsByAccountId_ReturnsOperationDtoList() {

        Long accountId = 1L;
        List<Operation> operations = List.of(new Operation(), new Operation());
        when(operationRepository.findByAccountId(accountId)).thenReturn(operations);

        List<OperationDto> operationDtos = List.of(new OperationDto(), new OperationDto());
        when(operationMapper.toOperationDto(any(Operation.class))).thenReturn(operationDtos.get(0), operationDtos.get(1));

        List<OperationDto> result = operationService.getOperationsByAccountId(accountId);

        assertEquals(operationDtos.size(), result.size());
        verify(operationRepository, times(1)).findByAccountId(accountId);
        verify(operationMapper, times(operations.size())).toOperationDto(any(Operation.class));
    }

    @Test
    void createOperation_CallsOperationRepositorySave() {
        Operation operation = new Operation();

        operationService.createOperation(operation);

        verify(operationRepository, times(1)).save(operation);
    }
}