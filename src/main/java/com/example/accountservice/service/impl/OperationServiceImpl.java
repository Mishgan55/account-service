package com.example.accountservice.service.impl;

import com.example.accountservice.dto.OperationDto;
import com.example.accountservice.entity.Operation;
import com.example.accountservice.mapper.OperationMapper;
import com.example.accountservice.repository.OperationRepository;
import com.example.accountservice.service.OperationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;


    public OperationServiceImpl(OperationRepository operationRepository, OperationMapper operationMapper) {
        this.operationRepository = operationRepository;
        this.operationMapper = operationMapper;
    }

    @Override
    public List<OperationDto> getOperationsByAccountId(Long accountId) {
        return operationRepository
                .findByAccountId(accountId)
                .stream()
                .map(operationMapper::toOperationDto)
                .toList();
    }

    @Override
    @Transactional
    public void createOperation(Operation operation) {
        operationRepository.save(operation);
    }

}
