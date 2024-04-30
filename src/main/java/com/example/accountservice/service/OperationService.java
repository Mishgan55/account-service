package com.example.accountservice.service;

import com.example.accountservice.dto.OperationDto;
import com.example.accountservice.entity.Operation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OperationService {
    List<OperationDto> getOperationsByAccountId(Long accountId);

    void createOperation(Operation operation);
}
