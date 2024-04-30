package com.example.accountservice.controller;

import com.example.accountservice.dto.OperationDto;
import com.example.accountservice.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/operation")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @Operation(summary = "Get operations by account ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = OperationDto.class))
    })
    @GetMapping("/byAccountId/{accountId}")
    public ResponseEntity<List<OperationDto>> getOperationsByAccountId(
            @Parameter(description = "Account ID", required = true) @PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(operationService.getOperationsByAccountId(accountId));
    }
}
